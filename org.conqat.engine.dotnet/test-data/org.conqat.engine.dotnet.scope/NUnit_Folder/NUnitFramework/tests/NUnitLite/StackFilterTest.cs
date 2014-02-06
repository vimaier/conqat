// *****************************************************
// Copyright 2006, Charlie Poole
//
// Licensed under the Open Software License version 3.0
// *****************************************************

using System;
using NUnitLite.Framework;

namespace NUnitLite.Tests
{
    [TestFixture]
    class StackFilterTest
    {
        private static readonly string rawTrace =
    @"   at NUnitLite.Framework.Assert.Fail(String message) in D:\Dev\NUnitLite\NUnitLite\Framework\Assert.cs:line 56" + Environment.NewLine +
    @"   at NUnitLite.Framework.Assert.That(String label, Object actual, Matcher expectation, String message) in D:\Dev\NUnitLite\NUnitLite\Framework\Assert.cs:line 50" + Environment.NewLine +
    @"   at NUnitLite.Framework.Assert.That(Object actual, Matcher expectation) in D:\Dev\NUnitLite\NUnitLite\Framework\Assert.cs:line 19" + Environment.NewLine +
    @"   at NUnitLite.Tests.GreaterThanMatcherTest.MatchesGoodValue() in D:\Dev\NUnitLite\NUnitLiteTests\GreaterThanMatcherTest.cs:line 12" + Environment.NewLine;

        private static readonly string filteredTrace =
    @"   at NUnitLite.Tests.GreaterThanMatcherTest.MatchesGoodValue() in D:\Dev\NUnitLite\NUnitLiteTests\GreaterThanMatcherTest.cs:line 12" + Environment.NewLine;

        [Test]
        public void FilterFailureTrace()
        {
            Assert.That( StackFilter.Filter( rawTrace ), Is.EqualTo( filteredTrace ) );
        }
    }
}
