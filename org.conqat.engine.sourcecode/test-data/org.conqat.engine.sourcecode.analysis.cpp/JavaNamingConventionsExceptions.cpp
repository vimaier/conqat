

// The following lines test ignoring IDM enforced naming violations
DM_Integer A_B_C(A* b, C* d) { /* Bla */ } // Ignore
GM_Integer A_B_C(A* b, C* d) { /* Bla */ } // Don't inore
DM_Float A_B_C(A* b, C* d) { /* Bla */ } // Ignore
void A_B_C(DM_Integer* b, C* d) { /* Bla */ } // Don't inore

// The following lines test ignoring BASICS enforced naming violations
void MY_METHOD(A_B_C* data) { /* Bla */ } // Ignore
void MY_METHOD(A_B_C* dat) { /* Bla */ } // Don't inore
void MY_METHOD(C* nothing, A_B_C* data) { /* Bla */ } // Ignore
data MY_METHOD(C* nothing, A_B_C* dat) { /* Bla */ } // Don't inore
