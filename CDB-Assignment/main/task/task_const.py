Y_N = 'Y_N'
M_TO_O = 'M_TO_O'
M_TO_M = 'M_TO_M'
FREE = 'FREE'
COLLECT = 'COLLECT'

import amt
import cf
import cc

platforms = {
    "AMT": amt,
    "CC": cc,
    "CF": cf
}

default_platform = 'AMT'

free_sep = '::'
