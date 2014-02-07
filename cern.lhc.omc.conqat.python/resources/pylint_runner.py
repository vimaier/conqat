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

VALID_EXIT_CODES = [0, 30, 31]


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
