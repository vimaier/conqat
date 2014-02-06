/*
 * NOTE: AS HERE MUCH DATA IS MOVED, I USE 32 BIT VALUES, BUT DON'T CARE
 *       MUCH ABOUT ENDIANESS. SO THIS PROBABLY WON'T RUN ON BIG ENDIAN MACHINES.
 *
 * buffer_work.c  - here are all the big conversion and filterign functions
 *
 * Copyright (C) 2002 Benjamin Hummel (benjamin@datamaze.de)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */

#include <SDL/SDL.h>
#include <libraw1394/raw1394.h>
#include <libdc1394/dc1394_control.h>

#include "somedefs.h"
#include "my_profiler.h"

/* these are in main.c*/
extern dc1394_cameracapture camera;
extern raw1394handle_t handle;
extern SDL_Surface *screen;

extern int width;
extern int height;
extern rgba *new_buf;
extern rgba *old_buf;
extern rgba *render_buf;
extern BYTE *dmask_buf;

extern BOOL running;
extern int effect;
extern BOOL use_deltamask;
extern BOOL use_grayscale;
extern rgba color1;
extern rgba color2;
extern rgba dmask_color;
extern BYTE dmask_param;
extern BYTE param;
extern BOOL verbose;
extern BOOL use_dma;

/* exchange old_buf and new_buf */
void
toggle_buffers (void)
{
	rgba *temp = new_buf;
	new_buf = old_buf;
	old_buf = temp;
}

/* capture from dc1394 and put it into new_buf.
   here is the conversion from YUV411 to RGB and the deltamask part. */
void
fill_buffer (void)
{
#define NORM(x) x = (x<0)?0:((x>255)?255:x)

	/* perform iso check - useful for multiple instances of the same cam */
	dc1394bool_t iso_on;
	if (dc1394_get_iso_status (handle, camera.node, &iso_on) != DC1394_SUCCESS)
	{
		fprintf (stderr, "Could not check iso status\n");
		running = FALSE; return;
	}
	if (iso_on != DC1394_TRUE)
	{
		printf ("Iso status was off. Trying to start again.\n");
		if (dc1394_start_iso_transmission (handle, camera.node) != DC1394_SUCCESS)
		{
			fprintf(stderr, "Could not start isochronous transmission\n");
			running = FALSE; return;
		}
	}

	VERBOSE_PRINTF ("Performing capture ... ");
	if (use_dma)
	{
		myprofiler_start (3, "    dc1394_dma_single_capture");
		if (dc1394_dma_single_capture (&camera) != DC1394_SUCCESS)
		{
			fprintf (stderr, "Could not capture from camera!\n");
			running = FALSE;
			return;
		}
		myprofiler_stop (3);
	}
	else
	{
		myprofiler_start (3, "    dc1394_single_capture");
		if (dc1394_single_capture (handle, &camera) != DC1394_SUCCESS)
		{
			fprintf (stderr, "Could not capture from camera!\n");
			running = FALSE;
			return;
		}
		myprofiler_stop (3);
	}
	VERBOSE_PRINTF ("done\n");

	if (use_grayscale)
	{
		register unsigned long *s = (unsigned long *)(camera.capture_buffer);
		register unsigned long *d = (unsigned long *)(new_buf);
		register unsigned long t1, t2;
		register unsigned long *l = (unsigned long *)(((BYTE *)s) + ((width*height*6)/4));

		while (s < l)
		{
#define PUTGRAY(x,y) t2=(t1&(x))>>(y); *d++ = t2|(t2<<8)|(t2<<16)|(t2<<24)
			t1 = *s++;
			PUTGRAY(0x0000ff00, 8);
			PUTGRAY(0x00ff0000,16);
			t1 = *s++;
			PUTGRAY(0x000000ff,0);
			PUTGRAY(0x0000ff00,8);
			PUTGRAY(0xff000000,24);
			t1 = *s++;
			PUTGRAY(0x000000ff,0);
			PUTGRAY(0x00ff0000,16);
			PUTGRAY(0xff000000,24);
#undef PUTGRAY
		}

	}
	else
	{
		register unsigned long *cam = (unsigned long *)(camera.capture_buffer);
		register unsigned long *nb  = (unsigned long *)new_buf;
		unsigned long *lim = nb + width*height;
		unsigned long c;
		unsigned long y0, y1;
		int rt, gt, bt;
		int u, v, vr, ub, vg, ug;

		/* most of the following code is derived from a macro used in
		   conversions.c in coriander.

		   I think the following people had some part in it:
		   	Dan Dennedy, Bart Nabbe, Damien Douxchamps */

#define NORM_RGBT rt=(rt<0)?0:((rt>255)?255:rt); \
		  gt=(gt<0)?0:((gt>255)?255:gt); \
		  bt=(bt<0)?0:((bt>255)?255:bt)
#define COMP_COL(_y,_vr,_ug,_vg,_ub) rt = _y + _vr; gt = _y - _ug - _vg; bt = _y + _ub; \
  NORM_RGBT; *nb++ = (_y<<24)|(bt<<16)|(gt<<8)|(rt)

		while (nb < lim)
		{
			c = *cam++;
			u  =  c & 0x000000ff;
			y0 = (c & 0x0000ff00)>>8;
			y1 = (c & 0x00ff0000)>>16;
			v  = c>>24;

			u -= 128; v -= 128;
			vr = (((v << 10) + (v << 8) + (v << 7) + (v << 4) + (v << 3) + (v << 1)) >> 11);
			ug = ((u << 8) + (u << 7) + (u << 4) + (u << 2) + (u << 1)) >> 11;
			vg = (((v << 9) + (v << 6) + (v << 4) + (v << 1) + v) >> 11);
			ub = (((u << 11) + (u << 5) - (u << 1)) >> 11);

			COMP_COL(y0, vr, ug, vg, ub);
			COMP_COL(y1, vr, ug, vg, ub);

			c = *cam++;
			y0 =  c & 0x000000ff;
			y1 = (c & 0x0000ff00)>>8;

			COMP_COL(y0, vr, ug, vg, ub);
			COMP_COL(y1, vr, ug, vg, ub);

			u  = (c & 0x00ff0000)>>16;
			y0 = c>>24;
			c = *cam++;
			y1 =  c & 0x000000ff;
			v  = (c & 0x0000ff00)>>8;

			u -= 128; v -= 128;
			vr = (((v << 10) + (v << 8) + (v << 7) + (v << 4) + (v << 3) + (v << 1)) >> 11);
			ug = ((u << 8) + (u << 7) + (u << 4) + (u << 2) + (u << 1)) >> 11;
			vg = (((v << 9) + (v << 6) + (v << 4) + (v << 1) + v) >> 11);
			ub = (((u << 11) + (u << 5) - (u << 1)) >> 11);

			COMP_COL(y0, vr, ug, vg, ub);
			COMP_COL(y1, vr, ug, vg, ub);

			y0 = (c& 0x00ff0000)>>16;
			y1 = c>>24;

			COMP_COL(y0, vr, ug, vg, ub);
			COMP_COL(y1, vr, ug, vg, ub);
		}
#undef COMP_COL
#undef NORM_RGBT
	}

#undef NORM

	if (use_dma)
	{
		VERBOSE_PRINTF ("Freeing DMA buffer ... ");
		dc1394_dma_done_with_buffer (&camera);
		VERBOSE_PRINTF ("done\n");
	}
}

/* uses the old_buf and new_buf buffers and fills render_buf depending on effect */
void
apply_effect_filter (void)
{
	register unsigned long *n = (unsigned long *)new_buf;
	register unsigned long *o = (unsigned long *)old_buf;
	register unsigned long *r = (unsigned long *)render_buf;
	register unsigned long *lim = (unsigned long *)(((BYTE*)render_buf) + width*height*4);
	register unsigned long c1 = *((unsigned long *)(&color1));
	register unsigned long c2 = *((unsigned long *)(&color2));

	switch (effect)
	{
		case FX_NONE:
			memcpy (render_buf, new_buf, width*height*sizeof(rgba));
			break;

		case FX_DELTA:
			if (use_grayscale) /* use quick and dirty method */
			{
				while (r < lim)
				{
					c1 = *n++;
					c2 = *o++;
					c1 = (c1>c2)?(c1-c2):(c2-c1);
					*r++ = c1;
				}
			}
			else /* long clean way */
			{
				register unsigned long t1,t2,t3;

				while (r < lim)
				{
					c1 = *n++;
					c2 = *o++;

					t1 = c1 & 0xff;
					t2 = c2 & 0xff;
					t3 = (t1>t2)?(t1-t2):(t2-t1);

					t1 = c1 & 0xff00;
					t2 = c2 & 0xff00;
					t1 = (t1>t2)?(t1-t2):(t2-t1);
					t3 |= (t1&0xff00);

					t1 = c1 & 0xff0000;
					t2 = c2 & 0xff0000;
					t1 = (t1>t2)?(t1-t2):(t2-t1);
					t3 |= (t1&0xff0000);

					*r++ = t3;
				}
			}
			break;

		case FX_SOBEL:
			/* sobel only uses the gray values */
			{
				int x, t1, t2, o = (width-2)%3;
				int lev = param*param;
				register unsigned long *p1, *p2, *p3, *p4, *p5, *p6, *p7, *p8, *p9;

				/* put black line */
				for (x = 0; x < width; x++) *r++ = 0;

				for (x = 2; x < height; x++)
				{
					*r++ = 0;
					p1 = n + (x-2)*width; p2 = p1+1; p3 = p2+1;
					p4 = n + (x-1)*width; p5 = p4+1; p6 = p5+1;
					p7 = n + x*width;     p8 = p7+1; p9 = p8+1;
					lim = p1 + width -2 -o;
					while (p1 < lim)
					{
						t1 = (*p1>>24) + (*p2>>23) + (*p3>>24);
						t1 -= ((*p7>>24) + (*p8>>23) + (*p9>>24));
						t2 = (*p1>>24) + (*p4>>23) + (*p7>>24);
						t2 -= ((*p3>>24) + (*p6>>23) + (*p9>>24));
						t1 = t1*t1+t2*t2;
						*r++ = (t1>lev)?c1:c2;
						p1 += 3; p4 += 3; p7 += 3;

						t1 = (*p2>>24) + (*p3>>23) + (*p1>>24);
						t1 -= ((*p8>>24) + (*p9>>23) + (*p7>>24));
						t2 = (*p2>>24) + (*p5>>23) + (*p8>>24);
						t2 -= ((*p1>>24) + (*p4>>23) + (*p7>>24));
						t1 = t1*t1+t2*t2;
						*r++ = (t1>lev)?c1:c2;
						p2 += 3; p5 += 3; p8 += 3;

						t1 = (*p3>>24) + (*p2>>23) + (*p1>>24);
						t1 -= ((*p9>>24) + (*p8>>23) + (*p7>>24));
						t2 = (*p3>>24) + (*p6>>23) + (*p9>>24);
						t2 -= ((*p2>>24) + (*p5>>23) + (*p8>>24));
						t1 = t1*t1+t2*t2;
						*r++ = (t1>lev)?c1:c2;
						p3 += 3; p6 += 3; p9 += 3;
					}
					if (o == 2)
					{
						*r++ = 0;
						*r++ = 0;
					}
					else if (o == 1)
					{
						*r++ = 0;
					}
					*r++ = 0;
				}

				/* put black line */
				for (x = 0; x < width; x++) *r++ = 0;
			}
			break;

		case FX_AND:
			while (r < lim)
			{
				*r++ = (*n++) & c1;
			}
			break;

		case FX_XOR:
			while (r < lim)
			{
				*r++ = (*n++) ^ c1;
			}
			break;
		case FX_COLORIZE:
		case FX_COLINV:
			{
				unsigned long palette[256];
				rgba *pal_rgba = (rgba *)palette;
				int i;

				/* setup palette */
				for (i = 0; i < 128; i++)
				{
					pal_rgba[i].r = (((unsigned long)(color1.r))*i)>>7;
					pal_rgba[i].g = (((unsigned long)(color1.g))*i)>>7;
					pal_rgba[i].b = (((unsigned long)(color1.b))*i)>>7;
					pal_rgba[i+128].r = color1.r +
						((((unsigned long)(255-color1.r))*i)>>7);
					pal_rgba[i+128].g = color1.g +
						((((unsigned long)(255-color1.g))*i)>>7);
					pal_rgba[i+128].b = color1.b +
						((((unsigned long)(255-color1.b))*i)>>7);
				}

				/* apply effect */
				if (effect == FX_COLORIZE)
				{
					while (r < lim)
					{
						*r++ = palette[(*n++)>>24];
					}
				}
				else /* FX_COLINV */
				{
					while (r < lim)
					{
						*r++ = palette[(*n++ ^ 0xff000000)>>24];
					}
				}
			}
			break;
		case FX_COLBLEND:
			{
				unsigned long palette[256];
				rgba *pal_rgba = (rgba *)palette;
				int i;

				/* setup palette */
				for (i = 0; i < 256; i++)
				{
					pal_rgba[i].r = color1.r +
						(((((int)(color2.r))-((int)(color1.r)))*i)>>8);
					pal_rgba[i].g = color1.g +
						(((((int)(color2.g))-((int)(color1.g)))*i)>>8);
					pal_rgba[i].b = color1.b +
						(((((int)(color2.b))-((int)(color1.b)))*i)>>8);
				}

				/* apply effect */
				while (r < lim)
				{
					*r++ = palette[(*n++)>>24];
				}
			}
			break;
		default:
			fprintf (stderr, "Invalid filter selected.\n");
			running = FALSE;
	}
}

/* Update the SDL surface with the new render_buf */
void
display_buffer (void)
{
	SDL_Surface *surf = 0;

	surf = SDL_CreateRGBSurfaceFrom (
		render_buf, width, height, 32, width*4,
		0x000000ff, 0x0000ff00, 0x00ff0000, 0) ;
	if (surf == 0)
	{
		fprintf (stderr, "Could not create temporary SDL surface.\n");
		running = FALSE; return;
	}

	if (SDL_BlitSurface (surf, 0, screen, 0) != 0)
	{
		fprintf (stderr, "Could not blit into window.\n");
		running = FALSE; return;
	}

	SDL_UpdateRect (screen, 0, 0, width, height);
	SDL_FreeSurface (surf);
}

/* apply coloramp effect (working only in new_buf) */
void
do_coloramp (void)
{
	register unsigned long *n = (unsigned long *)new_buf;
	register unsigned long *lim = (unsigned long *)(((BYTE*)render_buf) + width*height*4);

	while (n < lim)
	{
		int r, g, b, h, s, v;
		int min, max, delta;

		// get rgb
		r = *n & 0xff;
		g = (*n & 0xff00) >> 8;
		b = (*n & 0xff0000) >> 16;

		// Split to HSV

		min = r;
		max = r;
		if (g > max) max = g;
		else if (g < min) min = g;
		if (b > max) max = b;
		else if (b < min) min = b;

		v = max;
		delta = max - min;

		if ((max == 0) || (delta == 0))
		{
			s = h = 0;
		}
		else
		{
			s = (delta*256) / max;

			if (r == max) h = ((g - b)*10000) / delta;
			else if (g == max) h = 2*10000 + ((b-r)*10000) / delta;
			else h = 4*10000 + ((r-g)*10000) / delta;

			h = (h * 256) / (10000 * 6);
			while (h < 0) h += 256;
		}

		// adjust
		if (s < 30) s = 0; else s = 255;

		// convert back

		if (s == 0)
		{
			r = g = b = v;
		}
		else
		{
			int i = (h * 6) / (256);
			int f = ((h * 6 * 10000) / 256) - i * 10000;
			int p = (v * ( 256 - s )) / 256;
        		int q = (v * ( 256*10000 - s * f )) / (256 * 10000);
        		int t = (v * ( 256*10000 - s * ( 10000 - f ) )) / (256*10000);

			switch( i ) {
			case 0:
				r = v;
				g = t;
				b = p;
				break;
			case 1:
				r = q;
				g = v;
				b = p;
				break;
			case 2:
				r = p;
				g = v;
				b = t;
				break;
			case 3:
				r = p;
				g = q;
				b = v;
				break;
			case 4:
				r = t;
				g = p;
				b = v;
				break;
			case 5:
				r = v;
				g = p;
				b = q;
				break;
			default:
				printf ("Programmers error no 1\n");
        		}
		}

		// set rgb, but keep gray
		*n &= 0xff000000;
		*n |= r | (g << 8) | (b << 16);

		n++;
	}
}


