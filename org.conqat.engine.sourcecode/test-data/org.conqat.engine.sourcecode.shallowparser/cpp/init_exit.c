/*
 * init_exit.c  - startup and cleanup routines
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

#include <stdlib.h>
#include <stdio.h>
#include <SDL/SDL.h>
#include <libdc1394/dc1394_control.h>
#include <libraw1394/raw1394.h>
#include "somedefs.h"

/* these are in main.c */
extern BOOL SDL_loaded;
extern SDL_Surface *screen;
extern BOOL fullscreen;
extern BOOL nowindow;
extern BOOL verbose;
extern BOOL use_dma;

extern raw1394handle_t handle;
extern nodeid_t *camera_nodes;
extern dc1394_cameracapture camera;
extern BOOL iso_started;
extern BOOL camera_aquired;
extern int camera_num;

extern int width;
extern int height;
extern rgba *new_buf;
extern rgba *old_buf;
extern rgba *render_buf;

extern char *outputfile;


/* should be called before exit */
void
cleanup (void)
{
	/* shutdown ieee1394 */
	if (iso_started)
	{
		VERBOSE_PRINTF ("Stopping iso transmission ... ");
		if (dc1394_stop_iso_transmission(handle,camera.node) != DC1394_SUCCESS)
			fprintf(stderr, "Error at stopping ISO.\n");
		VERBOSE_PRINTF ("done\n");
	}
	if (camera_aquired)
	{
		VERBOSE_PRINTF ("Releasing camera ... ");
		if ( use_dma )
			dc1394_dma_release_camera(handle,&camera);
		else dc1394_release_camera(handle,&camera);
		VERBOSE_PRINTF ("done\n");
	}

	if (handle != 0)
	{
		VERBOSE_PRINTF ("Destroying raw1394 handle ... ");
		raw1394_destroy_handle(handle);
		VERBOSE_PRINTF ("done\n");
	}

	/* shutdown SDL */
	if (SDL_loaded)
	{
		VERBOSE_PRINTF ("Shutting down SDL ... ");
		SDL_Quit();
		VERBOSE_PRINTF ("done\n");
	}

	/* delete buffers */
	VERBOSE_PRINTF ("Freeing memory ... ");
	if (new_buf != 0)    free (new_buf);
	if (old_buf != 0)    free (old_buf);
	if (render_buf != 0) free (render_buf);
	VERBOSE_PRINTF ("done\n");
}

/* allocate memory for internal buffers */
BOOL
setup_buffers (void)
{
	VERBOSE_PRINTF ("Allocating memory ... ");

	/* keep buffers larger, to simplify some filters */
	new_buf = (rgba *) malloc (width * height * sizeof (rgba) +20);
	old_buf = (rgba *) malloc (width * height * sizeof (rgba) +20);
	render_buf = (rgba *) malloc (width * height * sizeof (rgba) +20);

	if (outputfile == 0) /* apply default */
	{
		outputfile = (char *)malloc (strlen (DEFAULT_OUTPUT) +1);
		strcpy (outputfile, DEFAULT_OUTPUT);
	}

	if ((new_buf == 0) || (old_buf == 0) || (render_buf == 0) || (outputfile == 0))
	{
		fprintf (stderr, "Error: Not enough memory!\n");
		return FALSE;
	}

	VERBOSE_PRINTF ("done\n");
	return TRUE;
}

/* init the IEEE1394 part */
BOOL
start_1394 (void)
{
	int numcams;

	VERBOSE_PRINTF ("Creating 1394 handle ... ");
	handle = dc1394_create_handle(0);
	if (handle == 0)
	{
		fprintf (stderr, "Could not create 1394 handle!\nDid you load the modules?\n");
		return FALSE;
	}
	VERBOSE_PRINTF ("done\n");

	VERBOSE_PRINTF ("Scanning for cameras ... ");
	camera_nodes = dc1394_get_camera_nodes (handle, &numcams, 0);
	if (numcams < 1)
	{
		fprintf (stderr, "No cameras found!\n");
		return FALSE;
	}
	VERBOSE_PRINTF ("done\n");

	if (camera_num >= numcams)
	{
		printf ("Camera number too big. Using camera 0 instead.\n");
		camera_num = 0;
	}

	/* clear camera */
	memset (&camera, 0, sizeof(camera));

	VERBOSE_PRINTF ("Setting up camera ... ");
	if ( use_dma )
	{
#if 0  // FIXME
		/* always use this given format */
		if (dc1394_dma_setup_capture (handle, camera_nodes[camera_num],
				camera_num, /* use different channels for different cameras */
				FORMAT_VGA_NONCOMPRESSED,
				MODE_640x480_YUV411, SPEED_400,
				FRAMERATE_30, 10 /*dma buffers*/, &camera) != DC1394_SUCCESS)
		{
			fprintf (stderr, "Could not setup desired video format!\n");
			return FALSE;
		}
#endif
	}
	else
	{
		/* always use this given format */
		if (dc1394_setup_capture (handle, camera_nodes[camera_num],
				camera_num, /* use different channels for different cameras */
				FORMAT_VGA_NONCOMPRESSED,
				MODE_640x480_YUV411, SPEED_400,
				FRAMERATE_30, &camera) != DC1394_SUCCESS)
		{
			fprintf (stderr, "Could not setup desired video format!\n");
			return FALSE;
		}
	}
	VERBOSE_PRINTF ("done\n");

	camera_aquired = TRUE;
	return TRUE;
}

/* init SDL */
BOOL
start_SDL (void)
{
	VERBOSE_PRINTF ("Setting up SDL ... ");

	if (nowindow) /* init timer and return */
	{
		if (SDL_Init (SDL_INIT_TIMER)!= 0)
		{
			fprintf(stderr, "Could not initialize SDL: %s.\n", SDL_GetError());
			return FALSE;
		}
		else SDL_loaded = TRUE;
		return TRUE;
	}

	if (SDL_Init (SDL_INIT_VIDEO | SDL_INIT_TIMER)!= 0)
	{
		fprintf(stderr, "Could not initialize SDL: %s.\n", SDL_GetError());
		return FALSE;
	}
	else SDL_loaded = TRUE;

	screen = SDL_SetVideoMode (width, height, 16,
		SDL_HWSURFACE | SDL_ANYFORMAT | (fullscreen ? SDL_FULLSCREEN : 0));
	if (screen == 0)
	{
		fprintf (stderr, "Could not set video mode: %s.\n", SDL_GetError());
		return FALSE;
	}

	SDL_WM_SetCaption (APP_NAME " " APP_VERSION, 0);

	VERBOSE_PRINTF ("done\n");

	return TRUE;
}

