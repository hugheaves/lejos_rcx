# Makefile

WEBSITETARFILE=/usr/tmp/lejos-site.tar

release: clean apidocs
	tar cvf ${WEBSITETARFILE} *
	gzip -f ${WEBSITETARFILE}

apidocs:
	mkdir -p apidocs
	javadoc -author -d apidocs -sourcepath "`cygpath -w ${LEJOS_ROOT}/classes`" java.io java.lang java.util josx.platform.rcx josx.util
	
clean:
	rm -f `find . -name '*~'`
	rm -rf apidocs
