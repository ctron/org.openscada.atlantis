<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:import href="../../../docbook/xhtml/chunk.xsl"/>
  <xsl:param name="admon.graphics" select="1"/>
  <xsl:param name="admon.graphics.extension">.png</xsl:param>
  <xsl:param name="admon.graphics.path">docbook/images/</xsl:param>
  <xsl:param name="section.autolabel" select="1"/>
  <xsl:param name="section.label.includes.component.label" select="1"/>
  
  <!-- callouts-->
  <xsl:param name="callout.graphics">0</xsl:param>
  <xsl:param name="callout.unicode">1</xsl:param>
  
  <!-- These extensions are required for table printing and other stuff -->
  <xsl:param name="use.extensions">1</xsl:param>
  <xsl:param name="tablecolumns.extension">0</xsl:param>
  <xsl:param name="callout.extensions">1</xsl:param>
  
  	<!-- custom logo -->
	<xsl:template name="article.titlepage.before.recto">
		<div align="center">
    		<img src="docbook/images/logo-openscada-web.png"/>
  		</div>
	</xsl:template>
</xsl:stylesheet>
