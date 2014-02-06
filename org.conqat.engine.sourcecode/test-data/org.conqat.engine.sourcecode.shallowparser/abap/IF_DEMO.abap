*"* components of interface IF_DEMO
interface IF_DEMO
  public .


  interfaces IF_DEMO_BASE .

  data x type string read-only .

  interface IF_DEMO_RULES load .
  methods GET
    returning
      value(RULES) type IF_DEMO_RULES=>GT_RULES_T
    raising
      CX_ROOT .
endinterface.