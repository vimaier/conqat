/*
 * my_profiler.c  - simple routines to measure the time consumption of some code
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
#include <SDL/SDL.h>
#include <string.h>
#include "my_profiler.h"


/* To keep things simple, I'm using fixed sizes */
#define MYPROFILER_DATASIZE 20
#define MYPROFILE_STRINGLEN 255

/* storage area for internal data */
typedef struct myprofiler_data_t_struct {
	int min, max, avg, count;
	unsigned long lasttime;
	char name[MYPROFILE_STRINGLEN+1];
} myprofiler_data_t;

myprofiler_data_t myprofiler_data[MYPROFILER_DATASIZE];

/* init the "profiler" */
void
myprofiler_init (void)
{
	int i;
	for (i = 0; i < MYPROFILER_DATASIZE; i++)
	{
		myprofiler_data[i].min     = 1000000;
		myprofiler_data[i].max     = 0;
		myprofiler_data[i].avg     = 0;
		myprofiler_data[i].count   = 0;
		myprofiler_data[i].name[0] = 0;
	}
}

/* start the timer for one block,
   id is a unique number between 0 and MYPROFILER_DATASIZE-1 to identify the code block,
   name is a name used for the statistics */
void
myprofiler_start (int id, char *name)
{
	if (name == 0) return;
	if (name[0] == 0) return;
	if (strlen(name) > MYPROFILE_STRINGLEN) return;
	if (id >= MYPROFILER_DATASIZE) return;

	if (myprofiler_data[id].name[0] == 0)
	{
		strcpy (myprofiler_data[id].name, name);
	}
	myprofiler_data[id].lasttime = SDL_GetTicks();
}

/* stop the timer for the block,
   id should have the same value as in the call to myprofiler_start */
void
myprofiler_stop (int id)
{
	unsigned long time;

	if (id >= MYPROFILER_DATASIZE) return;

	time = SDL_GetTicks() - myprofiler_data[id].lasttime;

	if (time < myprofiler_data[id].min)
		myprofiler_data[id].min = time;
	if (time > myprofiler_data[id].max)
		myprofiler_data[id].max = time;

	time = time + myprofiler_data[id].avg * myprofiler_data[id].count;
	myprofiler_data[id].count += 1;
	myprofiler_data[id].avg = time / myprofiler_data[id].count;
}

/* print out some statistics */
void
myprofiler_print (void)
{
	int i, j;
	int maxlen = 0;

	for (i = 0; i < MYPROFILER_DATASIZE; i++)
	{
		if (strlen (myprofiler_data[i].name) > maxlen)
			maxlen = strlen (myprofiler_data[i].name);
	}

	if (maxlen == 0)
	{
		printf ("\nNo profiling data\n\n");
		return;
	}

	printf ("\nBegin of profiling data:\n");

	for (j=0; j < (maxlen+4); j++) printf (" ");

	printf ("MIN   MAX   AVG   COUNT \n");

	for (i = 0; i < MYPROFILER_DATASIZE; i++)
	{
		if (myprofiler_data[i].name[0] != 0)
		{
			printf ("  %s: ", myprofiler_data[i].name);
			for (j=strlen(myprofiler_data[i].name); j < maxlen; j++) printf (" ");

			printf ("%-5d %-5d %-5d %-5d\n", myprofiler_data[i].min,
				myprofiler_data[i].max, myprofiler_data[i].avg,
				myprofiler_data[i].count);
		}
	}
	printf  ("\nEnd of profiling data.\n\n");
}
