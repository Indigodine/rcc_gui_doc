TARGET:=org.junit.runner.JUnitCore AllTests
TARGET_EMMA:=-XX:-UseSplitVerifier -Demma.report.columns=name,line,method -Demma.verbosity.level=quiet emmarun -r html -sp $(SRCDIR):$(TESTDIR) -f -ix +jeu.*,+reseau.*,outils.*,mj.*,joueur.*,gui.* $(CLASSPATH) $(TARGET)
TARGET_RUN:=run.Run
ARGS=

CC:=javac

SRCDIR:=src
OBJDIR:=bin
LIBDIR:=lib
TESTDIR:=test
DOCDIR:=javadoc

SRC:=$(filter-out %/package-info.java, $(shell find $(SRCDIR) -name "*.java"))
TEST:=$(shell find $(TESTDIR) -name "*.java")
OBJ:=$(patsubst $(SRCDIR)/%.java,$(OBJDIR)/%.class,$(SRC))
OBJ+=$(patsubst $(TESTDIR)/%.java,$(OBJDIR)/%.class,$(TEST))

CLASSPATH:=-classpath $(shell find $(LIBDIR) -name "*.jar" -printf "%p:")$(OBJDIR)
JVM_OPTIONS:=-enableassertions
RUN_OPTIONS:=$(CLASSPATH) $(JVM_OPTIONS) $(TARGET) $(ARGS)
RUN_OPTIONS_EMMA:=$(CLASSPATH) $(JVM_OPTIONS) $(TARGET_EMMA) $(ARGS)
RUN_OPTIONS_RUN:=$(CLASSPATH) $(JVM_OPTIONS) $(TARGET_RUN) $(ARGS)
JAVADOC_OPTIONS:=$(CLASSPATH) -sourcepath $(SRCDIR) -d $(DOCDIR) -charset "utf-8" -link http://docs.oracle.com/javase/7/docs/api/ -use -quiet

WARN:=-Xlint:all
COMPILE_OPTIONS:=$(WARN) -d $(OBJDIR) -sourcepath $(SRCDIR):$(TESTDIR) $(CLASSPATH)

HOTSWAPPER_JAVELEON:=-XX:-UseSplitVerifier -Xbootclasspath/p:/tmp/JaveleonBoot/ -javaagent:lib/javeleon.jar
HOTSWAPPER_AGENTSMITH:=-javaagent:lib/smith-1.0.jar=`pwd`/$(OBJDIR)
HOTSWAPPER:=$(HOTSWAPPER_JAVELEON)

all: compile

run: compile
	java $(RUN_OPTIONS_RUN)
run_cover: compile
	java $(RUN_OPTIONS_EMMA)


# ECLIPSE_PATH=/usr/bin/eclipse
# format:
# 	$(ECLIPSE_PATH) -nosplash -application org.eclipse.jdt.core.JavaCodeFormatter -verbose -config .settings/org.eclipse.jdt.core.prefs src/outils

javadoc: $(SRC)
	javadoc $(JAVADOC_OPTIONS) $(SRC)

compile: $(OBJDIR) $(OBJ)

$(OBJDIR)/%.class: $(SRCDIR)/%.java
	@echo $< "-->" $@
	@$(CC) $(COMPILE_OPTIONS) $<
$(OBJDIR)/%.class: $(TESTDIR)/%.java
	@echo $< "-->" $@
	@$(CC) $(COMPILE_OPTIONS) $<

$(OBJDIR):
	test -d $(OBJDIR) || mkdir $(OBJDIR)

clean:
	rm -fr $(OBJDIR)
	rm -fr coverage
