/* package */class ProjectFileParser8 extends ProjectFileParser {

	/** {@inheritDoc} */
	@Override
	protected ProjectFileReader8 createReader(File projectFile,
			String encoding) {
		return new ProjectFileReader8(projectFile);
	}