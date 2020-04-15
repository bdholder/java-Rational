junit_standalone := junit-platform-console-standalone-1.6.0.jar
junitc := javac -classpath .:$(junit_standalone)
junit := java -classpath .:$(junit_standalone)
args := org.roundrockisd.stonypoint.test.SimpleConsoleLauncher --classpath . --exclude-engine=junit-vintage

VPATH := src

# class files
sphs := org/roundrockisd/stonypoint
Rational := $(sphs)/Rational.class
RationalTests := $(sphs)/test/RationalTests.class
SimpleConsoleLauncher := $(sphs)/test/SimpleConsoleLauncher.class

.PHONY: clean test

test: $(Rational) $(RationalTests) $(SimpleConsoleLauncher)
	$(junit) $(args) --scan-class-path

clean:
	@find . -name '*.class' -type f -delete
	@rm -r org

$(Rational): Rational.java
	javac -d . Rational.java

$(RationalTests): RationalTests.java
	$(junitc) -d . $^

$(SimpleConsoleLauncher): SimpleConsoleLauncher.java
	$(junitc) -d . $^