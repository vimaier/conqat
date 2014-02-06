/*
 * basic_control.c  - commandline parsing and key handling
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



#include <stdio.h>
#include <stdlib.h>
#include <SDL/SDL.h>
#include "somedefs.h"

/* these are in main.c */
extern int effect;
extern BOOL fullscreen;
extern BOOL use_grayscale;
extern rgba color1;
extern rgba color2;
extern BYTE param;
extern BOOL print_statistics;
extern BOOL running;
extern BOOL nowindow;
extern int camera_num;
extern BOOL verbose;
extern BOOL use_dma;
extern BOOL use_coloramp;

extern BOOL capturing;
extern int capture_mode;
extern unsigned long cap_param;
extern char *outputfile;

extern SDL_Event event;

extern char* effect_names[];

/* external function in buffer_work.c */
void fill_dmask_buffer (void);

/* helper func, converting char in hex to a rgba color (a is set 0)
   returns TRUE on success, otherwise FALSE */
BOOL
hex2rgba (char *hex, rgba *c)
{
	int i,j;
	if (strlen (hex) != 6) return FALSE;
	for (i = 0; i < 6; i++)
	{
		if (('0' <= hex[i]) && (hex[i] <= '9')) j = hex[i] - '0';
		else if (('a' <= hex[i]) && (hex[i] <= 'f')) j = hex[i] - 'a' + 10;
		else if (('A' <= hex[i]) && (hex[i] <= 'F')) j = hex[i] - 'A' + 10;
		else return FALSE;
		switch (i)
		{
			case 0: c->r = j*16; break;
			case 1: c->r += j; break;
			case 2: c->g = j*16; break;
			case 3: c->g += j; break;
			case 4: c->b = j*16; break;
			case 5: c->b += j; break;
			default:
				fprintf (stderr, "The impossible happened!"); return FALSE;
		}
	}
	c->a = 0;
	return TRUE;
}

/* print the help screen */
void
print_usage (void)
{
	int i;

	printf ("%s: Display and filter video from an IEEE1394 camera\n", APP_NAME);
	printf ("It can NOT be used with a camcorder!\n\n");
	printf ("Commandline options:\n");
	printf ("   -a, --autocapture   start with capturing on\n");
	printf ("   -c <value>          set camera number\n");
	printf ("   -c1 <color>         set primary color\n");
	printf ("   -c2 <color>         set secondary color\n");
	printf ("   --coloramp          start with coloramp on\n");
	printf ("   -d, --dma           use dma (video1394)\n");
	printf ("   -e <effect>         set filter effect (for a list of effects see below)\n");
	printf ("   -f, --fullscreen    use fullscreen mode\n");
	printf ("   -g, --grayscale     set grayscale mode\n");
	printf ("   -h, --help          display this help screen\n");
	printf ("   -n, --nowindow      suppress opening of a window (disables keys!)\n");
	printf ("   -o <file>           set output file for capturing\n");
	printf ("   -p <value>          set effect filter parameter\n");
	printf ("   -s, --statistics    output some internal statistics on exit\n");
	printf ("   -t <timing>         set capture timing\n");
	printf ("   -v, --verbose       print more output\n");
	printf ("\n");
	printf ("   <color> is a RRGGBB color given in hex\n");
	printf ("   <effect> is one of the following: %s", effect_names[FX_MIN]);
	for (i = FX_MIN+1; i <= FX_MAX; i++)
		printf (", %s", effect_names[i]);
	printf ("\n");
	printf ("   <file> is the prefix for the filename; a number and .jpg is appended\n");
	printf ("   <timing> is either \"single\", \"manual\" or a number, followed by f, m or s\n");
	printf ("   <value> is a number between 0 and 255\n");
	printf ("\nSee the README for fourther instructions.\n");
}

/* look for command line options */
BOOL
parse_commandline (int argc, char **argv)
{
#define CHECK_PARAM_OPTION(x) if (++i >= argc) { fprintf(stderr, x); return FALSE; }
	int i, j;
	for (i = 1; i < argc; i++)
	{
		if (strcmp (argv[i], "-a") == 0)
		{
			capturing = TRUE;
		}
		else if (strcmp (argv[i], "--autocapture") == 0)
		{
			capturing = TRUE;
		}
		else if (strcmp (argv[i], "-c") == 0)
		{
			CHECK_PARAM_OPTION ("-c option without param!\n");
			camera_num = atoi (argv[i]);
			if ((camera_num == 0) && (argv[i][0] != '0'))
			{
				fprintf (stderr, "Invalid camera number: %s\n", argv[i]);
				return FALSE;
			}
		}
		else if (strcmp (argv[i], "-c1") == 0)
		{
			CHECK_PARAM_OPTION ("-c1 option without color!\n");
			if (!hex2rgba (argv[i], &color1))
			{
				fprintf (stderr, "Invalid color: %s\n", argv[i]);
				return FALSE;
			}
		}
		else if (strcmp (argv[i], "-c2") == 0)
		{
			CHECK_PARAM_OPTION ("-c2 option without color!\n");
			if (!hex2rgba (argv[i], &color2))
			{
				fprintf (stderr, "Invalid color: %s\n", argv[i]);
				return FALSE;
			}
		}
		else if (strcmp (argv[i], "--coloramp") == 0)
		{
			use_coloramp = TRUE;
		}
		else if (strcmp (argv[i], "-d") == 0)
		{
			use_dma = TRUE;
		}
		else if (strcmp (argv[i], "--dma") == 0)
		{
			use_dma = TRUE;
		}
		else if (strcmp (argv[i], "-e") == 0)
		{
			CHECK_PARAM_OPTION ("-e option without effect!\n");
			effect = FX_MIN-1;
			for (j = FX_MIN; j <= FX_MAX; j++)
			{
				if (strcmp (argv[i], effect_names[j]) == 0)
				{
					effect = j;
				}
			}
			if (effect < FX_MIN)
			{
				fprintf (stderr, "Invalid effect: %s\n", argv[i]);
				return FALSE;
			}
		}
		else if ((strcmp (argv[i], "-f") == 0)
			  || (strcmp (argv[i], "--fullscreen") == 0))
		{
			fullscreen = TRUE;
		}
		else if ((strcmp (argv[i], "-g") == 0)
			  || (strcmp (argv[i], "--grayscale") == 0))
		{
			use_grayscale = TRUE;
		}
		else if ((strcmp (argv[i], "-h") == 0)
			  || (strcmp (argv[i], "--help") == 0))
		{
			print_usage();
			return FALSE;
		}
		else if (strcmp (argv[i], "-n") == 0)
		{
			nowindow = TRUE;
		}
		else if (strcmp (argv[i], "--nowindow") == 0)
		{
			nowindow = TRUE;
		}
		else if (strcmp (argv[i], "-o") == 0)
		{
			CHECK_PARAM_OPTION ("-o option without filename!\n");
			if (outputfile != 0) free (outputfile);
			outputfile = malloc (strlen (argv[i]) + 1);
			if (outputfile == 0)
			{
				fprintf (stderr, "Not enough memory!\n");
				return FALSE;
			}
			strcpy (outputfile, argv[i]);
		}
		else if (strcmp (argv[i], "-p") == 0)
		{
			CHECK_PARAM_OPTION ("-p option without parameter!\n");
			param = atoi (argv[i]);
			if ((param == 0) && (argv[i][0] != '0'))
			{
				fprintf (stderr, "Invalid parameter: %s\n", argv[i]);
				return FALSE;
			}
		}
		else if (strcmp (argv[i], "-s") == 0)
		{
			print_statistics = TRUE;
		}
		else if (strcmp (argv[i], "--statistics") == 0)
		{
			print_statistics = TRUE;
		}
		else if (strcmp (argv[i], "-t") == 0)
		{
			CHECK_PARAM_OPTION ("-t option without parameter!\n");
			if (strcmp (argv[i], "single") == 0)
			{
				capture_mode = CAP_SINGLE;
			}
			else if (strcmp (argv[i], "manual") == 0)
			{
				capture_mode = CAP_MANUAL;
			}
			else
			{
				char c;
				if (strlen (argv[i]) < 2)
				{
					fprintf (stderr, "Invalid timing: %s\n", argv[i]);
					return FALSE;
				}
				c = argv[i][strlen(argv[i])-1];
				argv[i][strlen(argv[i])-1] = 0;
				cap_param = atoi (argv[i]);
				if (cap_param == 0)
				{
					fprintf (stderr, "Invalid timing: %s\n", argv[i]);
					return FALSE;
				}
				switch (c)
				{
					case 'f': capture_mode = CAP_FRAMES; break;
					case 'm': capture_mode = CAP_TIME; break;
					case 's': capture_mode = CAP_TIME;
						cap_param *= 1000; break;
					default:
						fprintf (stderr, "Invalid timing: %s\n", argv[i]);
						return FALSE;
				}
			}
		}
		else if (strcmp (argv[i], "-v") == 0)
		{
			verbose = TRUE;
		}
		else if (strcmp (argv[i], "--verbose") == 0)
		{
			verbose = TRUE;
		}
		else
		{
			fprintf (stderr, "Unknown option: %s\n\n", argv[i]);
			print_usage();
			return FALSE;
		}
	}

	return TRUE;
#undef CHECK_PARAM_OPTION
}

/* handle keys */
void
key_handler (void)
{
#define TOGGLE(x) if (x) x = FALSE; else x = TRUE
#define SET_FX(x) if ((FX_MIN+(x)-1) <= FX_MAX) effect = FX_MIN+(x)-1
	if (event.type == SDL_KEYDOWN)
	{
		switch (event.key.keysym.sym)
		{
			case SDLK_ESCAPE:
			case SDLK_q: running = FALSE; break;

			case SDLK_SPACE:
			case SDLK_c: TOGGLE(capturing); break;

			case SDLK_g: TOGGLE(use_grayscale); break;

			case SDLK_a: TOGGLE (use_coloramp); break;

			case SDLK_F1:  SET_FX(1);  break;
			case SDLK_F2:  SET_FX(2);  break;
			case SDLK_F3:  SET_FX(3);  break;
			case SDLK_F4:  SET_FX(4);  break;
			case SDLK_F5:  SET_FX(5);  break;
			case SDLK_F6:  SET_FX(6);  break;
			case SDLK_F7:  SET_FX(7);  break;
			case SDLK_F8:  SET_FX(8);  break;
			case SDLK_F9:  SET_FX(9);  break;
			case SDLK_F10: SET_FX(10); break;
			case SDLK_F11: SET_FX(11); break;
			case SDLK_F12: SET_FX(12); break;

			case SDLK_UP:
				if (event.key.keysym.mod & KMOD_CTRL)
				{
					if (param != 255) param += 1;
				}
				else
				{
					param = (param>=(255-STEPSIZE))?255:(param+STEPSIZE);
				}
				break;
			case SDLK_DOWN:
				if (event.key.keysym.mod & KMOD_CTRL)
				{
					if (param != 0) param -= 1;
				}
				else
				{
					param = (param<=STEPSIZE)?0:(param-STEPSIZE);
				}
				break;

			case SDLK_RIGHT:
				effect += 1;
				if (effect > FX_MAX) effect = FX_MIN;
				break;
			case SDLK_LEFT:
				effect -= 1;
				if (effect < FX_MIN) effect = FX_MAX;
				break;

#define SETCOL(x) if(event.key.keysym.mod&KMOD_SHIFT) \
	{rgba c=x; color2.r=c.r; color2.g=c.g; color2.b=c.b;} else \
	{rgba c=x; color1.r=c.r; color1.g=c.g; color1.b=c.b;} break
			case SDLK_1: SETCOL(RED);
			case SDLK_2: SETCOL(GREEN);
			case SDLK_3: SETCOL(BLUE);
			case SDLK_4: SETCOL(CYAN);
			case SDLK_5: SETCOL(MAGENTA);
			case SDLK_6: SETCOL(YELLOW);
			case SDLK_7: SETCOL(BLACK);
			case SDLK_8: SETCOL(WHITE);
#undef SETCOL

#define STEP_COL(col, stepmul) \
	{ int val, step;\
	if(event.key.keysym.mod&KMOD_CTRL) step = 1; else step = STEPSIZE; \
	step *= (stepmul); val = col; val += step; \
	val = (val > 255)?255:val; val = (val < 0)?0:val; col = val; }
#define USECOL2 event.key.keysym.mod&KMOD_SHIFT

			case SDLK_u: if(USECOL2) STEP_COL(color2.r,1) else STEP_COL(color1.r,1) break;
			case SDLK_i: if(USECOL2) STEP_COL(color2.g,1) else STEP_COL(color1.g,1) break;
			case SDLK_o: if(USECOL2) STEP_COL(color2.b,1) else STEP_COL(color1.b,1) break;
			case SDLK_j: if(USECOL2) STEP_COL(color2.r,-1) else STEP_COL(color1.r,-1) break;
			case SDLK_k: if(USECOL2) STEP_COL(color2.g,-1) else STEP_COL(color1.g,-1) break;
			case SDLK_l: if(USECOL2) STEP_COL(color2.b,-1) else STEP_COL(color1.b,-1) break;
#undef STEP_COL
#undef USECOL2

			case SDLK_x: /* xchange colors */
				{
					rgba temp = color1;
					color1 = color2;
					color2 = temp;
				} break;

			default: /* no default */
		}
	}
	else if (event.type == SDL_KEYUP)
	{
		/* nothing to handle yet */
	}
	else
	{
		fprintf (stderr, "Key handler called from some strange place\n");
	}
#undef SET_FX
#undef TOGGLE
}

/* update the title of the SDL window */
void
update_caption (void)
{
	static char s[500];
	static unsigned long lasttime = 0;
	unsigned int c1 = (color1.r<<16) | (color1.g<<8) | (color1.b);
	unsigned int c2 = (color2.r<<16) | (color2.g<<8) | (color2.b);

	unsigned long time = SDL_GetTicks();
	int fps = time - lasttime;

	if (fps != 0) fps = 1000 / fps;
	lasttime = time;

	sprintf (s, "%s efct: %s, colamp: %s, gry: %s, c1: %06x, c2: %06x, parm: %02x, cpt: %s, fps: %02d ",
		APP_NAME " " APP_VERSION, effect_names[effect],
		use_coloramp ? "on" : "off",
		use_grayscale ? "on" : "off",
		c1, c2, (int)param,
		capturing ? "yes" : "no",
		fps);

	SDL_WM_SetCaption (s, 0);
}
