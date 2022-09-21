
beamer:
	asciidoctor -b docbook dsl_talk.adoc -o foobar.docbook
	pandoc foobar.docbook -t beamer -o pres.pdf

reveal:
	# install with: npm i --save asciidoctor @asciidoctor/reveal.js
	 npx asciidoctor-revealjs dsl_talk.adoc
