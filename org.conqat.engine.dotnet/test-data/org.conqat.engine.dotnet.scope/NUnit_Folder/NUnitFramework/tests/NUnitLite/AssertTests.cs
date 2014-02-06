// *****************************************************
// Copyright 2006, Charlie Poole
//
// Licensed under the Open Software License version 3.0
// *****************************************************

using System;
using NUnit.Framework;
using NUnitLite.Framework;
using NUnitLite.Constraints;

namespace NUnitLite.Tests
{
    [TestFixture]
    public class AssertTests
    {
        [Test]
        public void AssertPassesIfMatcherMatches()
        {
            Assert.That( 4, new AlwaysMatcher() );
        }

        [Test, ExpectedException(typeof(AssertionException))]
        public void AssertFailsIfMatcherFails()
        {
            Assert.That(4, new NeverMatcher());
        }

        //[Test, ExpectedException(typeof(AssertionException),Handler="MessageChecker1")]
        public void HasValidMessageFormat()
        {
            Assert.That(42, Is.Null);
        }

        private static void MessageChecker1(Exception ex)
        {
            Assert.That(ex.Message, Is.EqualTo(
                Msgs.Pfx_Expected + "null" + Environment.NewLine +
                Msgs.Pfx_Actual   + "42" + Environment.NewLine));
        }

        //[Test, ExpectedException(typeof(AssertionException), Handler = "MessageChecker2")]
        public void CanSupplyUserMessage()
        {
            Assert.That(42, Is.Null, "Wrong answer!");
        }

        private static void MessageChecker2(Exception ex)
        {
            Assert.That(ex.Message, Is.EqualTo(
                "  Wrong answer!" + Environment.NewLine +
                Msgs.Pfx_Expected + "null" + Environment.NewLine +
                Msgs.Pfx_Actual   + "42" + Environment.NewLine));
        }

        //[Test, ExpectedException(typeof(AssertionException), Handler = "MessageChecker3")]
        public void CanSupplyUserMessageWithParams()
        {
            Assert.That(42, Is.Null, "{0} is the wrong answer, {1}!", 42, "fool");
        }

        private static void MessageChecker3(Exception ex)
        {
            Assert.That(ex.Message, Is.EqualTo(
                "  42 is the wrong answer, fool!" + Environment.NewLine +
                Msgs.Pfx_Expected + "null" + Environment.NewLine +
                Msgs.Pfx_Actual   + "42" + Environment.NewLine));
        }

        class AlwaysMatcher : Constraint
        {
            public override bool Matches(object actual)
            {
                return true;
            }

            public override void WriteDescriptionTo(MessageWriter writer)
            {
                writer.Write("always");
            }
        }

        class NeverMatcher : Constraint
        {
            public override bool Matches(object actual)
            {
                return false;
            }

            public override void WriteDescriptionTo(MessageWriter writer)
            {
                writer.Write("never");
            }
        }
    }
}
