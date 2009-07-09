<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format" 
	version='1.0'>
  <xsl:import href="../../../docbook/fo/docbook.xsl"/>
  <xsl:param name="admon.graphics" select="1"/>
  <xsl:param name="admon.graphics.extension">.svg</xsl:param>
  <xsl:param name="admon.graphics.path">docbook/images/</xsl:param>
  <xsl:param name="callout.unicode">1</xsl:param>
  <xsl:param name="section.autolabel" select="1"/>
  <xsl:param name="section.label.includes.component.label" select="1"/>
  <xsl:param name="paper.type">A4</xsl:param>
  <xsl:param name="draft.mode">no</xsl:param>
  
  <!-- These extensions are required for table printing and other stuff -->
  <xsl:param name="use.extensions">1</xsl:param>
  <xsl:param name="tablecolumns.extension">0</xsl:param>
  <xsl:param name="callout.extensions">1</xsl:param>
  <!-- FOP provide only PDF Bookmarks at the moment -->
  <xsl:param name="fop.extensions">0</xsl:param>
  <xsl:param name="fop1.extensions">1</xsl:param>
  
  	<!-- custom logo -->
	<xsl:template name="article.titlepage.before.recto">
		<fo:block text-align="center">
    		<fo:external-graphic src="docbook/images/logo-openscada-pdf.png"/>
  		</fo:block>
	</xsl:template>
  
</xsl:stylesheet>