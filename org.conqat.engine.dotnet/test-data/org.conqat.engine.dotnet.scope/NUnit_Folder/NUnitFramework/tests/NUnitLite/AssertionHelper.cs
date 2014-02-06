using System;
using NUnitLite.Constraints;

namespace NUnitLite.Framework
{
    class AssertionHelper : Is
    {
        #region Expect
        public static void Expect( bool expression )
        {
            Expect(expression, True, null);
        }

        public static void Expect(bool expression, string message)
        {
            Expect(expression, True, message);
        }

        public static void Expect(object actual, Constraint constraint)
        {
            Expect(actual, constraint, null);
        }

        public static void Expect(object actual, Constraint constraint, string message)
        {
            Assert.That(actual, constraint, message);
        }

        public static void Expect(object actual, Constraint constraint, string message, params object[] args)
        {
            Assert.That(actual, constraint, message, args);
        }

        public static void Expect(object actual, Type type)
        {
            Assert.That(actual, Is.InstanceOfType(type));
        }
        #endregion

        #region String Helpers
        public static Constraint StartsWith(string substring)
        {
            return new StartsWithConstraint(substring);
        }

        public static Constraint EndsWith(string substring)
        {
            return new EndsWithConstraint(substring);
        }
        #endregion

        #region Collection Helpers
        #endregion
    }
}
