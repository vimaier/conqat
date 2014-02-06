*"* components of interface IF_WITH_ALIASES
interface IF_WITH_ALIASES
  public .

  class CL_SOME_CLASS definition load .

  interfaces IF_WITH_ALIASES_BASE .

  aliases CHECK
    for IF_WITH_ALIASES_BASE~CHECK .
  aliases HAS_CHANGES
    for IF_WITH_ALIASES_BASE~HAS_CHANGES .
  aliases INITIALIZE
    for IF_WITH_ALIASES_BASE~INITIALIZE .
  aliases SAVE
    for IF_WITH_ALIASES_BASE~SAVE .

  methods GET_VALUE
    returning
      value(VAL) type string .
  methods SET_VALUE
    importing
      !VAL type string .
endinterface.