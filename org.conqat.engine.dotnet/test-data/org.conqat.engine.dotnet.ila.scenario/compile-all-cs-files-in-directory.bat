# This script compiles all C# files in the working directory.
# Use it to compile a newly added test data file.

# Try to put C# compiler onto path
SET PATH=%PATH%;C:\WINDOWS\Microsoft.NET\Framework\v2.0.50727;C:\WINDOWS\Microsoft.NET\Framework\v1.1.4322;C:\WINDOWS\Microsoft.NET\Framework\v1.0.3705;

# Compile all C# files in this directory
FOR %%F in (*.cs) DO csc /target:library /debug+ %%F
