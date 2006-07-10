<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version='1.0'
  >

  <xsl:template match="message">
    <sect3>
      <title>
        <xsl:value-of select="@name"/> ( <xsl:value-of select="@code"/> )
      </title>
      <sect4><title>Overview</title>
      
      <xsl:apply-templates select="description"/>
      
      <para>
        <variablelist><title>Message overview</title>
          <xsl:choose>
            <xsl:when test="@source='any'">
              <varlistentry><term>Source</term>
              <listitem>
                <para>Message can be sent by client or server</para>
              </listitem>
              </varlistentry>
            </xsl:when>
            <xsl:when test="@source='client'">
              <varlistentry><term>Source</term>
              <listitem>
                <para>Message can be sent by client</para>
              </listitem>
              </varlistentry>
            </xsl:when>
            <xsl:when test="@source='server'">
              <varlistentry><term>Source</term>
              <listitem>
                <para>Message can be sent by server</para>
              </listitem>
              </varlistentry>
            </xsl:when>
            <xsl:when test="@source=''"></xsl:when>
            <xsl:otherwise>
              <varlistentry><term>Source</term>
              <listitem>
                <para>Unknown message source: <xsl:value-of select="@source"/></para>
              </listitem>
              </varlistentry>
            </xsl:otherwise>
          </xsl:choose>

          <xsl:choose>
            <xsl:when test="@class=''">
            </xsl:when>
            <xsl:otherwise>
              <varlistentry><term>Class</term>
              <listitem>
                <para><xsl:value-of select="@class"/></para>
              </listitem>
              </varlistentry>
            </xsl:otherwise>
          </xsl:choose>
                   
        </variablelist>
      </para>
      </sect4>
      
      <sect4><title>Message payload</title>
        <xsl:apply-templates select="payload"/>
      </sect4>
      
      <xsl:apply-templates select="reply"/>
      
    </sect3>
  </xsl:template>
  
  <xsl:template match="reply">
    <sect4><title>Reply</title>
      <para>
        <xsl:apply-templates/>
      </para>
    </sect4>
  </xsl:template>
  
  <xsl:template match="description">
    <para>
      <xsl:apply-templates/>
    </para>
  </xsl:template>
  
  <xsl:template match="map">
    <formalpara><title>Map malue</title>
      <xsl:apply-templates select="description"/>
    </formalpara>
    
    <para>
    <variablelist><title>Map entries</title>
      <xsl:apply-templates select="entry"/>
    </variablelist>
    </para>
    
  </xsl:template>
  
  <xsl:template match="entry">
    <varlistentry>
      <term><xsl:value-of select="@key"/></term>
      <listitem>
        <xsl:apply-templates/>
      </listitem>
    </varlistentry>
  </xsl:template>
  
  <xsl:template match="string">
    <formalpara><title>String value</title>
      <xsl:apply-templates/>
    </formalpara>
  </xsl:template>
  
  <xsl:template match="void">
    <formalpara><title>Void value</title>
      <xsl:apply-templates select="description"/>
    </formalpara>
  </xsl:template>
  
  <xsl:template match="any">
    <formalpara><title>Any value</title>
      <xsl:apply-templates/>
    </formalpara>
  </xsl:template>
  
  <xsl:template match="list">
    <formalpara><title>List value</title>
      <xsl:apply-templates select="description"/>
      <itemizedlist>
        <xsl:apply-templates select="item"/>
      </itemizedlist>
    </formalpara>
  </xsl:template>
  
  <xsl:template match="item">
    <listitem>
      <xsl:apply-templates/>
    </listitem>
  </xsl:template>

</xsl:stylesheet>