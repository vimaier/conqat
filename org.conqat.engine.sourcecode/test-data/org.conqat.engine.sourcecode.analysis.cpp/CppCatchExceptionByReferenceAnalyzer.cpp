int foo () {

	int a, b, c;
	
	try {
		callMyMethod ();	
	} catch (int e) {
		printf ("error");
	} catch (int &e) {
		printf ("error");
	} catch (Exception e) {
		printf ("error");
	} catch (Exception *e) {
		printf ("error");
	} catch (Exception &e) {
		printf ("error");
	} catch (const Exception &e) {
		printf ("error");
	} catch (...) {
		printf ("error");
	}
}

