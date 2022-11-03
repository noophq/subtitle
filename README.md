Subtitle library
================

fr.noop.subtitle provides parsers and writers for different subtitle formats:

- vtt
- srt
- sami (smi)
- stl ebu
- ttml


VTT
---

Advanced features
=================

- voices (v tag)
- styles
- classes

Parser
======

Supported advanced features:

- voices
- styles
- classes

Usage:

    import fr.noop.subtitle.vtt.*;
    ...
    VttParser parser = new VttParser("utf-8");
    VttObject subtitle = parser.parse(new FileInputStream("/tmp/read/test.vtt"));

Writer
======

No advanced features implemented.

Usage:

    import fr.noop.subtitle.vtt.*;
    ...
    VttObject subtitle;
    ...
    VttWriter writer = new VttWriter("utf-8");
    writer.write(subtitle, new FileOutputStream("/tmp/write/test.vtt"));

SRT
---

Parser
======

Usage:

    import fr.noop.subtitle.srt.*;
    ...
    SrtParser parser = new SrtParser("utf-8");
    SrtObject subtitle = parser.parse(new FileInputStream("/tmp/read/test.srt"));

Writer
======

Usage:

    import fr.noop.subtitle.srt.*;
    ...
    SrtObject subtitle;
    ...
    SrtWriter writer = new SrtWriter("utf-8");
    writer.write(subtitle, new FileOutputStream("/tmp/write/test.srt"));

SAMI
----

Parser
======

Usage:

    import fr.noop.subtitle.sami.*;
    ...
    SamiParser parser = new SamiParser("utf-8");
    SamiObject subtitle = parser.parse(new FileInputStream("/tmp/read/test.smi"));

Writer
======

Usage:

    import fr.noop.subtitle.sami.*;
    ...
    SamiObject subtitle;
    ...
    SamiWriter writer = new SamiWriter("utf-8");
    writer.write(subtitle, new FileOutputStream("/tmp/write/test.smi"));

STL
---

The implemented STL format is the binary version of STL EBU.
Currently only the reader is provided.

All the specifications described in this document
https://tech.ebu.ch/docs/tech/tech3264.pdf
have been implemented, so you can get data from GSI and TTI blocks.

Advanced features
=================

- styles
- positioning

Parser
======

Usage:

    import fr.noop.subtitle.stl.*;
    ...
    StlParser parser = new StlParser();
    StlObject subtitle = parser.parse(new FileInputStream("/tmp/read/test.stl"));

TTML
----

Currently only the writer is provided.

Advanced features
=================

- styles
- regions

Parser
======

Usage:

    import fr.noop.subtitle.ttml.*;
    import fr.noop.subtitle.model.*;
    ...
    SubtitleObject subtitle;
    ...
    TtmlWriter writer = new TtmlWriter();
    writer.write(subtitle, new FileOutputStream("/tmp/write/test.ttml"));

Launch tests
------------

To launch tests:

    mvn test


Jar package
-----------

To create an executable jar using maven, run the following in the directory
where pom.xml is (Note: Maven must already be installed):

    mvn package

This will create the executable jar under target/subtitle-.*.jar

Convert from command line
-------------------------

Usage:

    java -jar subtitle-*.jar -i input-file -o output-file
