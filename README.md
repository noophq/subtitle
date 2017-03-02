Subtitle validation library
===========================

Copyright
---------
Copyright (C) 2017 Blackboard.

This program is based on noophq/subtitle library 
Copyright (C) 2015-2016 Cyrille Lebeaupin <clebeaupin@noop.fr>.

This program is free software licensed under the GNU Lesser General Public License v3. 
For the full copyright and license information, please view the LICENSE
file that was distributed with this source code.

Usage
-----
It provides validators for different subtitle formats:

- vtt
- srt

VTT Validator
-------------

    import com.blackboard.collaborate.validator.subtitle.*;
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

    import com.blackboard.collaborate.validator.subtitle.*;
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