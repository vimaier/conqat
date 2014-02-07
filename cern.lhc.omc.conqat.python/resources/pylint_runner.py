'''
.. module: pylint_runner
Created on 6 Feb 2014

Runs PyLint and overrides PyLints exit code. PyLint tends to return for every small warning a
non-zero exit code.
To suppress this behaviour this wrappper was written.

.. moduleauthor:: vimaier

'''

import sys
import os

VALID_EXIT_CODES = [0, 1, 2, 4, 8, 16, 30, 31]
""" 
From pylint --long-help 
  Output status code:
   Pylint should leave with following status code:
   * 0 if everything went fine
   * 1 if a fatal message was issued
   * 2 if an error message was issued
   * 4 if a warning message was issued
   * 8 if a refactor message was issued
   * 16 if a convention message was issued
   * 32 on usage error
   status 1 to 16 will be bit-ORed so you can know which different
   categories has been issued by analysing pylint output status code
   
Sometimes I received 30 and 31 so I added these values as well.
"""

#===================================================================================================
# main()-function
#===================================================================================================
def main():
    command = "pylint " + " ".join(sys.argv[1:])
    exit_code = os.system(command)

    if exit_code in VALID_EXIT_CODES:
        return 0
    else:
        return exit_code


#===================================================================================================
# main invocation
#===================================================================================================
if __name__ == "__main__":
    return_value = main()
    sys.exit(return_value)
