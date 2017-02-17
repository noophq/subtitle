Subtitle library
================

provides validators for different subtitle formats:

- vtt
- srt


VTT Validator
-------------

    import com.blackboard.collaborate.csl.validators.subtitle.*;
    ...
        
    try (SubtitleReader reader = new SubtitleReader(new FileInputStream(inputFile), inputCharset))) {
        ValidationReporterImpl reporter = new ValidationReporterImpl(reader);
        reporter.addValidationListener(new ValidationListener() {
            	@Override
            	public void onValidation(ValidationIssue issue) {
            	    // report validation issue
            		System.err.println(issue.toString());
            	}
            });

        VttParser parser = new VttParser(reporter, reader);
        
        SubtitleObject subtitles = parser.parse();
    } catch (IOException e) {
        // report IO error
    }

VTT Writer
----------

    import com.blackboard.collaborate.csl.validators.subtitle.*;
    ...
    SubtitleObject subtitles = parser.parse();
    ...
    
    try (Writer writer = new OutputStreamWriter(new FileOutputStream(outputFilePath), outputCharset)) {
        SubtitleWriter subWriter = new VttWriter(writer);
        subWriter.write(subtitles);
    } catch (IOException e) {
         // report IO error
    }

Command line
------------

Convert

    java -jar csl-validators-*.jar -i input-file.ext1 -o output-file.ext2

Validate

    java -jar csl-validators-*.jar -i input-file