<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version='1.0'>
	<xsl:import href="/usr/share/xml/docbook/stylesheet/nwalsh/fo/docbook.xsl"/>
	<xsl:param name="admon.graphics" select="1"/>
    <xsl:param name="admon.graphics.extension">.svg</xsl:param>
    <xsl:param name="admon.graphics.path">/usr/share/xml/docbook/stylesheet/nwalsh/images/</xsl:param>
	<xsl:param name="section.autolabel" select="1"/>
	<xsl:param name="section.label.includes.component.label" select="1"/>
    <xsl:param name="paper.type">A4</xsl:param>
    <xsl:param name="draft.mode">no</xsl:param>
</xsl:stylesheet>
