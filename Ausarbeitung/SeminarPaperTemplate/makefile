SHELL=/bin/bash

BASEFILE="template"

all:
	pdflatex -synctex=1 $(BASEFILE)
	bibtex $(BASEFILE)
	pdflatex -synctex=1 $(BASEFILE)
	pdflatex -synctex=1 $(BASEFILE)

clean:
	rm -f *.aux *.bbl *.blg *.glo *.idx *.log *.toc *.out *.synctex.gz

cleanall: clean
	rm -f *.ps *.pdf *.dvi
