// Tests the where clause

﻿namespace X.Y
{
    using System.Collections.Generic;

    internal static class Implant
    {
        internal static T PassedOrDefault<T>(this T callback, T defaultCallback) where T : class
        {
            return callback ?? defaultCallback;
        }
    }
}
