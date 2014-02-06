// First clone instance: 16 stmts
SourceCodeElement element = new SourceCodeElement(filename,
				ELanguage.JAVA);
IElementProvider<ISourceCodeElement> elementProvider = new SourceCodeElementProvider(
		null);
TokenProvider tokenProvider = new TokenProvider(elementProvider);
TokenNormalization tokenNormalization = new TokenNormalization(
		tokenProvider, new ArrayList<ITokenConfiguration>(),
		new TokenConfigurationDef().process());
Object statementNormalization = new StatementNormalization(
		tokenNormalization, false);

CloneDetector detector = new CloneDetector();
detector.init(new ProcessorInfoMock());
detector.setInput(element);
detector
		.setNormalization((IUnitProvider<IFileSystemElement, IUnit>) statementNormalization);
detector.setMinLength(minLength);
CloneDetectionResultElement result = detector.process();

RfssAnnotator annotator = new RfssAnnotator();
annotator.init(new ProcessorInfoMock());
annotator.setRoot(result);
annotator.process();

return (Double) result.getValue(RfssAnnotator.RFSS_KEY);

// Second clone instance
SourceCodeElement element = new SourceCodeElement(filename,
		ELanguage.JAVA);
IElementProvider<ISourceCodeElement> elementProvider = new SourceCodeElementProvider(
null);
TokenProvider tokenProvider = new TokenProvider(elementProvider);
TokenNormalization tokenNormalization = new TokenNormalization(
tokenProvider, new ArrayList<ITokenConfiguration>(),
new TokenConfigurationDef().process());
Object statementNormalization = new StatementNormalization(
tokenNormalization, false);

CloneDetector detector = new CloneDetector();
detector.init(new ProcessorInfoMock());
detector.setInput(element);
detector
.setNormalization((IUnitProvider<IFileSystemElement, IUnit>) statementNormalization);
detector.setMinLength(minLength);
CloneDetectionResultElement result = detector.process();

RfssAnnotator annotator = new RfssAnnotator();
annotator.init(new ProcessorInfoMock());
annotator.setRoot(result);
annotator.process();

return (Double) result.getValue(RfssAnnotator.RFSS_KEY);