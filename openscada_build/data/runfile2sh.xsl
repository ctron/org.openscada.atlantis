<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>
    <xsl:output
        method="text"
    />

<xsl:template match="/run">#!/bin/sh

CLASSPATH=&quot;&quot;
<xsl:apply-templates select="classpath/entry"/>
java -cp &quot;$CLASSPATH&quot; <xsl:apply-templates select="properties/entry"/> <xsl:value-of select="@main"/><xsl:text> </xsl:text><xsl:apply-templates select="arguments/entry"/>
<xsl:text>

</xsl:text>
</xsl:template>

<xsl:template match="classpath/entry">CLASSPATH=&quot;$CLASSPATH:<xsl:apply-templates/>&quot;<xsl:text>
</xsl:text></xsl:template>

<xsl:template match="properties/entry">&quot;-D<xsl:value-of select="@name"/>=<xsl:apply-templates/>&quot; </xsl:template>
<xsl:template match="arguments/entry">&quot;<xsl:apply-templates/>&quot; </xsl:template>

<xsl:template match="node()|@*"></xsl:template>


<xsl:template match="entry/text()">
  <xsl:copy>
    <xsl:apply-templates select="entry/text()"/>
  </xsl:copy>
</xsl:template>



</xsl:stylesheet>
