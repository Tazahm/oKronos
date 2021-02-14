<!-- Contains XSL directives to transform an XML document that contains the results of a match into a FO document. -->
<!-- The FO document can be interpreted latter to build a PDF document. -->
<!-- The output document conforms to the FFRS official match report. -->
<!--  -->
<!-- The input XML document contains a list of snapshots, each snapshot contains some class of data related to the result of the match. -->
<!-- The class of data is given by the element 'categoryId'. -->
<!--   - score-left, score-right : the score for, respectively the left (A) and right (B) team. -->
<!--   - penalty-left, penalty-right : the penalties for, respectively the left (A) and right (B) team. -->
<!--   - team-left, team-right : the players for, respectively the left (A) and right (B) team. -->
<!--   - match : miscellaneous match data. -->
<!--   - period : current period of the match  (unused). -->
<!--   - time : current time of the match  (unused). -->
<!--   - record : match events (unused) -->
<xsl:stylesheet version="2.0" 
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:fo="http://www.w3.org/1999/XSL/Format"
		exclude-result-prefixes="fo">
	<xsl:output method="xml" version="1.0" omit-xml-declaration="no" indent="yes"/>
	
	<xsl:param name="versionParam" select="'1.0'"/>
	
	<xsl:attribute-set name="left-cell-decoration">
		<xsl:attribute name="padding-top">1pt</xsl:attribute>
		<xsl:attribute name="border">0.5pt black solid</xsl:attribute>
		<xsl:attribute name="height">8pt</xsl:attribute>
	</xsl:attribute-set>
	
	<xsl:attribute-set name="right-cell-decoration">
		<xsl:attribute name="border">0.5pt black solid</xsl:attribute>
		<xsl:attribute name="padding-start">0.1cm</xsl:attribute>
		<xsl:attribute name="padding-end">0.1cm</xsl:attribute>
		<xsl:attribute name="padding-top">0.15cm</xsl:attribute>
		<xsl:attribute name="padding-bottom">0.15cm</xsl:attribute>
	</xsl:attribute-set>
	
	<xsl:attribute-set name="annexe-header-decoration">
		<xsl:attribute name="border">0.5pt black solid</xsl:attribute>
		<xsl:attribute name="padding-start">0.1cm</xsl:attribute>
		<xsl:attribute name="padding-end">0.1cm</xsl:attribute>
		<xsl:attribute name="padding-top">0.05cm</xsl:attribute>
		<xsl:attribute name="padding-bottom">0.05cm</xsl:attribute>
	</xsl:attribute-set>
	
	<xsl:attribute-set name="right-header-cell-decoration">
		<xsl:attribute name="border">0.5pt black solid</xsl:attribute>
		<xsl:attribute name="padding-top">0.10cm</xsl:attribute>
		<xsl:attribute name="padding-bottom">0.10cm</xsl:attribute>
	</xsl:attribute-set>
	
	<xsl:attribute-set name="right-remarks-cell-decoration">
		<xsl:attribute name="border">hidden</xsl:attribute>
		<xsl:attribute name="padding-top">0.05cm</xsl:attribute>
		<xsl:attribute name="padding-bottom">0.05cm</xsl:attribute>
	</xsl:attribute-set>
	
	<xsl:attribute-set name="font-cell-header">
		<xsl:attribute name="font-family">Helvetica</xsl:attribute>
		<xsl:attribute name="font-size">8</xsl:attribute>
		<xsl:attribute name="font-weight">bold</xsl:attribute>
		<xsl:attribute name="background-color">#CCCCCC</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>
	
	<xsl:attribute-set name="font-cell-body">
		<xsl:attribute name="font-family">Helvetica</xsl:attribute>
		<xsl:attribute name="font-size">6</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>
	
	<xsl:attribute-set name="font-title">
		<xsl:attribute name="font-family">Helvetica</xsl:attribute>
		<xsl:attribute name="font-size">7</xsl:attribute>
		<xsl:attribute name="font-weight">bold</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>
	
	<xsl:attribute-set name="font-header">
		<xsl:attribute name="font-family">Helvetica</xsl:attribute>
		<xsl:attribute name="font-size">8</xsl:attribute>
		<xsl:attribute name="font-weight">bold</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>
	
	<xsl:attribute-set name="font-remarques">
		<xsl:attribute name="font-family">Helvetica</xsl:attribute>
		<xsl:attribute name="font-size">8</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>
	
	<xsl:attribute-set name="font-annexe-title">
		<xsl:attribute name="font-family">Helvetica</xsl:attribute>
		<xsl:attribute name="font-size">12</xsl:attribute>
		<xsl:attribute name="font-weight">bold</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>
	
	<xsl:attribute-set name="font-annexe-header">
		<xsl:attribute name="font-family">Helvetica</xsl:attribute>
		<xsl:attribute name="font-size">9</xsl:attribute>
		<xsl:attribute name="font-weight">bold</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
	</xsl:attribute-set>
	
	<!-- Main template. -->
	<xsl:template match="okronos">
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format" font-family="Helvetica" font-size="7">
			<!-- color="red" -->
			<fo:layout-master-set>
				<fo:simple-page-master master-name="simpleA4" page-height="210mm" page-width="297mm" margin-top="04mm" margin-bottom="04mm" margin-left="04mm" margin-right="04mm">
					<fo:region-body/>
				</fo:simple-page-master>
			</fo:layout-master-set>
			<fo:page-sequence master-reference="simpleA4">
				<fo:flow flow-name="xsl-region-body">
					<xsl:call-template name="result-rec"/>
					<xsl:call-template name="annex"/>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>
	
	<!-- Result pages - call result display page per page as many time as needed. -->
	<xsl:template name="result-rec">
		<xsl:param name="startIndex" select="1"/>
		
		<xsl:call-template name="result">
			<xsl:with-param name="startIndex" select="$startIndex"/>
		</xsl:call-template>

		<xsl:variable name="next-index" select="$startIndex + 20"/>
		<xsl:variable name="left-mark-count" select="count(//snapshot[categoryId='score-left']/content/marks/mark[position() &gt;= $next-index])"/>
		<xsl:variable name="right-mark-count" select="count(//snapshot[categoryId='score-right']/content/marks/mark[position() &gt;= $next-index])"/>
		<xsl:variable name="left-penalty-count" select="count(//snapshot[categoryId='penalty-left']/content/penalties/penalty[position() &gt;= $next-index])"/>
		<xsl:variable name="right-penalty-count" select="count(//snapshot[categoryId='penalty-right']/content/penalties/penalty[position() &gt;= $next-index])"/>
		<xsl:variable name="total" select="$left-mark-count + $right-mark-count + $left-penalty-count + $right-penalty-count"/>
		<xsl:if test="$total &gt; 0">
			<xsl:call-template name="result-rec">
				<xsl:with-param name="startIndex" select="$next-index"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<!-- One page of result -->
	<xsl:template name="result">
		<xsl:param name="startIndex"/>
		<fo:block>
			<!-- border="1pt orange solid" -->
			<fo:inline-container inline-progression-dimension="225mm" vertical-align="top">
				<xsl:call-template name="blockLeft">
					<xsl:with-param name="startIndex" select="$startIndex"/>
				</xsl:call-template>
			</fo:inline-container>
			<fo:inline-container inline-progression-dimension="65mm" vertical-align="top">
				<xsl:call-template name="blockRight"/>
			</fo:inline-container>
		</fo:block>
	</xsl:template>
	
	<!-- The left block : the header and the team results. -->
	<xsl:template name="blockLeft">
		<xsl:param name="startIndex"/>
		<fo:block>
			<!-- border="1pt blue solid" -->
			<xsl:call-template name="header"/>
			<xsl:call-template name="team-result-A">
				<xsl:with-param name="startIndex" select="$startIndex"/>
			</xsl:call-template>
			<xsl:call-template name="team-result-B">
				<xsl:with-param name="startIndex" select="$startIndex"/>
			</xsl:call-template>
		</fo:block>
	</xsl:template>
	
	<!-- The right block : the aggregated results and miscellaneous data. -->
	<xsl:template name="blockRight">
		<xsl:param name="startIndex"/>
		<fo:block>
			<xsl:call-template name="teamNames"/>
			<xsl:call-template name="score"/>
			<xsl:call-template name="penalties-sumup"/>
			<xsl:call-template name="officials"/>
			<xsl:call-template name="logo"/>
			<xsl:call-template name="remarks"/>
		</fo:block>
	</xsl:template>
	
	<!-- The header : title and match related data. -->
	<xsl:template name="header">
		<fo:block xsl:use-attribute-sets="font-header" space-after="02mm">
			<!--   border="1pt green solid" -->
			<fo:inline-container inline-progression-dimension="49.9%" vertical-align="top">
				<xsl:apply-templates select="/okronos/snapshots/snapshot[categoryId='match']/content" mode="report"/>
			</fo:inline-container>
			<fo:inline-container inline-progression-dimension="49.9%" vertical-align="top">
				<xsl:call-template name="title"/>
			</fo:inline-container>
		</fo:block>
	</xsl:template>
	
	<!-- The title of the sheet. -->
	<xsl:template name="title">
		<fo:block font-weight="bold">
			<!-- border="1pt red solid" -->
			<fo:block text-align="center" font-size="9">
				RAPPORT OFFICIEL DE MATCH ROLLER HOCKEY
			</fo:block>
			<fo:block text-align="center">
				&#160;
			</fo:block>
			<fo:block text-align="center" font-size="8">
				Annexe à joindre obligatoirement à la feuille de match
			</fo:block>
		</fo:block>
	</xsl:template>
	
	<!-- The results for the team A (left). -->
	<xsl:template name="team-result-A">
		<xsl:param name="startIndex"/>
		<xsl:call-template name="team-result">
			<xsl:with-param name="startIndex" select="$startIndex"/>
			<xsl:with-param name="label" select="'A'"/>
			<xsl:with-param name="players" select="/okronos/snapshots/snapshot[categoryId='team-left']/content/players"/>
			<xsl:with-param name="marks" select="/okronos/snapshots/snapshot[categoryId='score-left']/content/marks"/>
			<xsl:with-param name="penalties" select="/okronos/snapshots/snapshot[categoryId='penalty-left']/content/penalties"/>
			<xsl:with-param name="timeouts" select="/okronos/snapshots/snapshot[categoryId='match']/content/team/left"/>
			<xsl:with-param name="swaps" select="/okronos/snapshots/snapshot[categoryId='match']/content/team/left/goalkeeperSwaps"/>
		</xsl:call-template>
	</xsl:template>
	
	<!-- The results for the team B (right). -->
	<xsl:template name="team-result-B">
		<xsl:param name="startIndex"/>
		<xsl:call-template name="team-result">
			<xsl:with-param name="startIndex" select="$startIndex"/>
			<xsl:with-param name="label" select="'B'"/>
			<xsl:with-param name="players" select="/okronos/snapshots/snapshot[categoryId='team-right']/content/players"/>
			<xsl:with-param name="marks" select="/okronos/snapshots/snapshot[categoryId='score-right']/content/marks"/>
			<xsl:with-param name="penalties" select="/okronos/snapshots/snapshot[categoryId='penalty-right']/content/penalties"/>
			<xsl:with-param name="timeouts" select="/okronos/snapshots/snapshot[categoryId='match']/content/team/right"/>
			<xsl:with-param name="swaps" select="/okronos/snapshots/snapshot[categoryId='match']/content/team/right/goalkeeperSwaps"/>
		</xsl:call-template>
	</xsl:template>
	
	<!-- The results for the team A or B : players, marks and penalties. -->
	<xsl:template name="team-result">
		<xsl:param name="startIndex"/>
		<xsl:param name="label"/>
		<xsl:param name="players"/>
		<xsl:param name="marks"/>
		<xsl:param name="penalties"/>
		<xsl:param name="timeouts"/>
		<xsl:param name="swaps"/>
		<fo:block space-before="02mm">
			<fo:inline-container inline-progression-dimension="85mm" vertical-align="top">
				<xsl:apply-templates select="$players">
					<xsl:with-param name="label" select="$label"/>
				</xsl:apply-templates>
			</fo:inline-container>
			<fo:inline-container inline-progression-dimension="60mm" vertical-align="top">
				<xsl:apply-templates select="$marks">
					<xsl:with-param name="startIndex" select="$startIndex"/>
				</xsl:apply-templates>
			</fo:inline-container>
			<fo:inline-container inline-progression-dimension="75mm" vertical-align="top">
				<fo:block>
					<xsl:apply-templates select="$penalties">
						<xsl:with-param name="startIndex" select="$startIndex"/>
					</xsl:apply-templates>
					<xsl:apply-templates select="$timeouts" mode="timeout">
					</xsl:apply-templates>
					<xsl:apply-templates select="$swaps">
					</xsl:apply-templates>
				</fo:block>
			</fo:inline-container>
		</fo:block>
	</xsl:template>	
	
	<!-- The players for the current team. -->
	<xsl:template match="//players">
		<xsl:param name="label"/>
		<fo:block>
			<!--  space-before="55mm" KO -->
			<fo:block font-weight="bold" xsl:use-attribute-sets="font-title">
				EQUIPE
				&#160;
				<xsl:value-of select="$label"/>
			</fo:block>
			<fo:table table-layout="fixed" width="100%" text-align="center" vertical-align="middle">
				<fo:table-column column-width="05mm"/>
				<fo:table-column column-width="20mm"/>
				<fo:table-column column-width="45mm"/>
				<fo:table-column column-width="10mm"/>
				<fo:table-header xsl:use-attribute-sets="font-cell-header">
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="left-cell-decoration">
							<fo:block>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="left-cell-decoration">
							<fo:block>
								N°Licence
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="left-cell-decoration">
							<fo:block>
								JOUEURS
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="left-cell-decoration">
							<fo:block>
								N°
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-header>
				<fo:table-body xsl:use-attribute-sets="font-cell-body">
					<!-- Goalkeepers -->
					<xsl:apply-templates select="player[goalkeeper='true'][position() &lt;= 2]" mode="player">
						<xsl:sort select="shirt" data-type="number" order="ascending" />
					</xsl:apply-templates>
					<!-- Empty rows for goalkeepers -->
					<xsl:call-template name="emptyGoalkeeperRow">
						<xsl:with-param name="indexRow" select="2-count(player[goalkeeper='true'])"/>
						<xsl:with-param name="indexCell" select="4"/>
					</xsl:call-template>
					
					<!-- Players -->
					<xsl:apply-templates select="player[official!='true'][goalkeeper!='true'][position() &lt;= 14]" mode="player">										    
					    <xsl:sort select="shirt" data-type="number" order="ascending" />
					</xsl:apply-templates>				
					<!-- Empty rows for players -->
					<xsl:call-template name="emptyRow">
						<xsl:with-param name="indexRow" select="14-count(player[official!='true'][goalkeeper!='true'])"/>
						<xsl:with-param name="indexCell" select="4"/>
					</xsl:call-template>
				</fo:table-body>
			</fo:table>
			<fo:table table-layout="fixed" width="100%" text-align="center" vertical-align="middle">
				<fo:table-column column-width="25mm"/>
				<fo:table-column column-width="45mm"/>
				<fo:table-column column-width="10mm"/>
				
				<fo:table-header xsl:use-attribute-sets="font-cell-header">
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="left-cell-decoration">
							<fo:block>
								N° Licence
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="left-cell-decoration">
							<fo:block>
								OFFICIELS D’EQUIPE
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="left-cell-decoration">
							<fo:block>
								&#160;
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-header>
				
				<fo:table-body xsl:use-attribute-sets="font-cell-body">
					<xsl:for-each select="player[official='true'][position() &lt;= 6]">
						 <xsl:sort order="ascending" data-type="number" select="shirt"/>
						<xsl:apply-templates select="." mode="official"/>
					</xsl:for-each>
					<xsl:call-template name="emptyRow">
						<xsl:with-param name="indexRow" select="6-count(player[official='true'])"/>
						<xsl:with-param name="indexCell" select="3"/>
					</xsl:call-template>
					
					<fo:table-row height="16pt">
						<fo:table-cell xsl:use-attribute-sets="left-cell-decoration" background-color="#CCCCCC">
							<fo:block>
								Signature de l’officiel
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="left-cell-decoration">
							<fo:block>
								&#160;
							</fo:block>
						</fo:table-cell>
					</fo:table-row>					
				</fo:table-body>
			</fo:table>
		</fo:block>
	</xsl:template>
	
	<!-- One payer into the table of players. -->
	<xsl:template match="//player" mode="player">
		<fo:table-row>
			<fo:table-cell xsl:use-attribute-sets="left-cell-decoration">
				<fo:block>
					<xsl:if test="goalkeeper = 'true'">
						G
					</xsl:if>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="left-cell-decoration">
				<fo:block>
					<xsl:value-of select="licence"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="left-cell-decoration">
				<fo:block>
					<xsl:value-of select="name"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="left-cell-decoration">
				<fo:block>
					<xsl:apply-templates select="shirt" mode="positiveInteger"/>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>
	
	<!-- One official into the table of officials. -->
	<xsl:template match="//player" mode="official">
		<fo:table-row>
			<fo:table-cell xsl:use-attribute-sets="left-cell-decoration">
				<fo:block>
					<xsl:value-of select="licence"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="left-cell-decoration">
				<fo:block>
					<xsl:value-of select="name"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="left-cell-decoration">
				<fo:block>
					<xsl:call-template name="official-position">
						<xsl:with-param name="value" select="shirt"/>
					</xsl:call-template>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>
	
	<!-- The table of score for the current team. -->
	<xsl:template match="//marks">
		<xsl:param name="startIndex"/>
		<fo:block>
			<fo:block xsl:use-attribute-sets="font-title" text-align="center">
				BUTS ET ASSISTANCES
			</fo:block>
			<fo:table table-layout="fixed" width="100%" text-align="center" vertical-align="middle">
				<fo:table-column column-width="17.5mm"/>
				<fo:table-column column-width="12.5mm"/>
				<fo:table-column column-width="12.5mm"/>
				<fo:table-column column-width="12.5mm"/>
				<fo:table-header xsl:use-attribute-sets="font-cell-header">
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="left-cell-decoration">
							<fo:block>
								Temps
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="left-cell-decoration">
							<fo:block>
								But
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="left-cell-decoration">
							<fo:block>
								Ass 1
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="left-cell-decoration">
							<fo:block>
								Ass 2
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-header>
				<fo:table-body xsl:use-attribute-sets="font-cell-body">
					<xsl:apply-templates select="mark[position() &gt;= $startIndex and position() &lt; $startIndex+20]"/>
					<xsl:call-template name="emptyRow">
						<xsl:with-param name="indexRow" select="20-count(mark[position() &gt;= $startIndex and position() &lt; $startIndex+20])"/>
						<xsl:with-param name="indexCell" select="4"/>
					</xsl:call-template>
					<fo:table-row height="16pt">
						<fo:table-cell xsl:use-attribute-sets="left-cell-decoration" text-align="left" number-columns-spanned="2" font-size="7" 
								padding-start="02mm" border-right-style="hidden">
							<fo:block font-weight="bold" padding-top="01mm" >
								SCORE FINAL
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="left-cell-decoration" text-align="left" number-columns-spanned="2" font-size="7" 
								border-left-style="hidden">
							<fo:block font-weight="bold" padding-top="01mm">
								<xsl:value-of select="count(mark)"/>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row height="27pt">
						<fo:table-cell xsl:use-attribute-sets="left-cell-decoration" number-columns-spanned="4" text-align="left" font-size="7" 
								padding-start="02mm" padding-top="01mm">
							<fo:block font-weight="bold">
								Signature du Capitaine
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</fo:block>
	</xsl:template>
	
	<!-- One mark into the table of score. -->
	<xsl:template match="//mark">
		<fo:table-row>
			<fo:table-cell xsl:use-attribute-sets="left-cell-decoration">
				<fo:block>
					<xsl:apply-templates select="time" mode="time"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="left-cell-decoration">
				<xsl:apply-templates select="scorer" mode="positiveContent"/>				
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="left-cell-decoration">
				<xsl:apply-templates select="assist1" mode="positiveContent"/>
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="left-cell-decoration">
				<xsl:apply-templates select="assist2" mode="positiveContent"/>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>
	
	<!-- The table of penalties for the current team. -->
	<xsl:template match="//penalties">
		<xsl:param name="startIndex"/>
		<fo:block xsl:use-attribute-sets="font-title" text-align="center">
			PENALITES
		</fo:block>
		<fo:table table-layout="fixed" width="100%" text-align="center" vertical-align="middle">
			<fo:table-column column-width="10mm"/>
			<fo:table-column column-width="10mm"/>
			<fo:table-column column-width="10mm"/>
			<fo:table-column column-width="15mm"/>
			<fo:table-column column-width="15mm"/>
			<fo:table-column column-width="15mm"/>
			<fo:table-header xsl:use-attribute-sets="font-cell-header">
				<fo:table-row>
					<fo:table-cell xsl:use-attribute-sets="left-cell-decoration">
						<fo:block>
							Temps
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="left-cell-decoration">
						<fo:block>
							N°
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="left-cell-decoration">
						<fo:block>
							Code
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="left-cell-decoration">
						<fo:block>
							Min
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="left-cell-decoration">
						<fo:block>
							Début
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="left-cell-decoration">
						<fo:block>
							Fin
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-header>
			<fo:table-body xsl:use-attribute-sets="font-cell-body">
				<xsl:apply-templates select="penalty[position() &gt;= $startIndex and position() &lt; $startIndex+20]"/>
				<xsl:call-template name="emptyRow">
					<xsl:with-param name="indexRow" select="20-count(penalty[position() &gt;= $startIndex and position() &lt; $startIndex+20])"/>
					<xsl:with-param name="indexCell" select="6"/>
				</xsl:call-template>
			</fo:table-body>
		</fo:table>
	</xsl:template>
	
	<!-- The table of timeouts for the current team. -->
	<xsl:template match="//*" mode="timeout">
		<fo:table table-layout="fixed" width="100%" text-align="center" vertical-align="middle">
			<fo:table-column column-width="10mm"/>
			<fo:table-column column-width="10mm"/>
			<fo:table-column column-width="10mm"/>
			<fo:table-column column-width="15mm"/>
			<fo:table-column column-width="15mm"/>
			<fo:table-column column-width="15mm"/>
			<fo:table-body xsl:use-attribute-sets="font-cell-body">
				<fo:table-row>
					<fo:table-cell xsl:use-attribute-sets="left-cell-decoration" number-columns-spanned="2" number-rows-spanned="2">
						<fo:block font-weight="bold">
							Temps morts
						</fo:block>
						<fo:block>
							&#160;
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="left-cell-decoration" number-columns-spanned="4" text-align="left">
						<fo:block font-weight="bold">
							<xsl:text>1/2 : </xsl:text>
							<xsl:apply-templates select="timeoutPeriod1" mode="time"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell xsl:use-attribute-sets="left-cell-decoration" number-columns-spanned="4" text-align="left">
						<fo:block font-weight="bold">
							<xsl:text>2/2 : </xsl:text>
							<xsl:apply-templates select="timeoutPeriod2" mode="time"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</xsl:template>
	
	<!-- The table of goalkeeper swap for the current team. -->
	<xsl:template match="//goalkeeperSwaps">
		<fo:table table-layout="fixed" width="100%" text-align="center" vertical-align="middle">
			<fo:table-column column-width="15mm"/>
			<fo:table-column column-width="10mm"/>
			<fo:table-column column-width="10mm"/>
			<fo:table-column column-width="10mm"/>
			<fo:table-column column-width="10mm"/>
			<fo:table-column column-width="10mm"/>
			<fo:table-column column-width="10mm"/>
			<fo:table-body xsl:use-attribute-sets="font-cell-body">
				<fo:table-row>
					<fo:table-cell xsl:use-attribute-sets="left-cell-decoration" number-rows-spanned="2">
						<fo:block font-weight="bold">
							Gardien
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="left-cell-decoration" text-align="center">
						<fo:block font-weight="bold">
							Temps
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="left-cell-decoration" text-align="center">
						<fo:block font-weight="bold">
							N°
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="left-cell-decoration" text-align="center">
						<fo:block font-weight="bold">
							Temps
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="left-cell-decoration" text-align="center">
						<fo:block font-weight="bold">
							N°
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="left-cell-decoration" text-align="center">
						<fo:block font-weight="bold">
							Temps
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="left-cell-decoration" text-align="center">
						<fo:block font-weight="bold">
							N°
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell xsl:use-attribute-sets="left-cell-decoration" text-align="center" height="16pt" padding-top="01mm">
						<xsl:apply-templates select="goalkeeperSwap[1]/time" mode="timeContent"/>						
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="left-cell-decoration" text-align="center" height="16pt" padding-top="01mm">
						<xsl:apply-templates select="goalkeeperSwap[1]/sheet" mode="positiveContent"/>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="left-cell-decoration" text-align="center" height="16pt" padding-top="01mm">
						<xsl:apply-templates select="goalkeeperSwap[2]/time" mode="timeContent"/>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="left-cell-decoration" text-align="center" height="16pt" padding-top="01mm">
						<xsl:apply-templates select="goalkeeperSwap[2]/sheet" mode="positiveContent"/>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="left-cell-decoration" text-align="center" height="16pt" padding-top="01mm">
						<xsl:apply-templates select="goalkeeperSwap[3]/time" mode="timeContent"/>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="left-cell-decoration" text-align="center" height="16pt" padding-top="01mm">
						<xsl:apply-templates select="goalkeeperSwap[3]/sheet" mode="positiveContent"/>
					</fo:table-cell>
				</fo:table-row>				
			</fo:table-body>
		</fo:table>
	</xsl:template>
	
	<!-- One penalty into the table of penalties. -->
	<xsl:template match="//penalty">
		<fo:table-row>
			<fo:table-cell xsl:use-attribute-sets="left-cell-decoration">
				<fo:block>
					<xsl:apply-templates select="penaltyTime" mode="time"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="left-cell-decoration">
				<fo:block>
					<xsl:value-of select="player"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="left-cell-decoration">
				<fo:block>
					<xsl:value-of select="code"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="left-cell-decoration">
				<fo:block>
					<xsl:value-of select="duration"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="left-cell-decoration">
				<xsl:apply-templates select="startTime" mode="timeContent"/>
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="left-cell-decoration">
				<xsl:apply-templates select="stopTime" mode="timeContent"/>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>
	
	<!-- The empty rows dedicated to the player table and goalkeeper rows (Put 'G' into the first cell). -->
	<xsl:template name="emptyGoalkeeperRow">
		<xsl:param name="indexRow" value="1"/>
		<xsl:param name="indexCell" value="4"/>
		<xsl:if test="$indexRow &gt; 0">
			<fo:table-row>
				<fo:table-cell xsl:use-attribute-sets="left-cell-decoration">
					<fo:block>
						G
					</fo:block>
				</fo:table-cell>
				<xsl:call-template name="emptyCells">
					<xsl:with-param name="indexCell" select="$indexCell - 1"/>
				</xsl:call-template>
			</fo:table-row>
			<xsl:call-template name="emptyGoalkeeperRow">
				<xsl:with-param name="indexRow" select="$indexRow - 1"/>
				<xsl:with-param name="indexCell" select="$indexCell"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<!-- Empty table rows. -->
	<xsl:template name="emptyRow">
		<xsl:param name="indexRow" value="1"/>
		<xsl:param name="indexCell" value="1"/>
		<xsl:if test="$indexRow &gt; 0">
			<fo:table-row>
				<xsl:call-template name="emptyCells">
					<xsl:with-param name="indexCell" select="$indexCell"/>
				</xsl:call-template>
			</fo:table-row>
			<xsl:call-template name="emptyRow">
				<xsl:with-param name="indexRow" select="$indexRow - 1"/>
				<xsl:with-param name="indexCell" select="$indexCell"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<!-- Empty cells into an empty table row. -->
	<xsl:template name="emptyCells">
		<xsl:param name="indexCell" value="1"/>
		<xsl:if test="$indexCell &gt; 0">
			<fo:table-cell xsl:use-attribute-sets="left-cell-decoration" background-color="#EEEEEE">
				<fo:block>
					&#160;
				</fo:block>
			</fo:table-cell>
			<xsl:call-template name="emptyCells">
				<xsl:with-param name="indexCell" select="$indexCell - 1"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<!-- Match related data -->
	<xsl:template match="//snapshot[categoryId='match']/content" mode="report">
		<fo:block font-weight="bold">
			<!-- border="1pt red solid" -->
			<fo:block>
				<fo:inline-container inline-progression-dimension="25%" vertical-align="top">
					<fo:block>
						N° match :
						<xsl:value-of select="matchNumber"/>
					</fo:block>
				</fo:inline-container>
				<fo:inline-container inline-progression-dimension="74.9%" vertical-align="top">
					<fo:block>
						Lieu :
						<xsl:value-of select="location"/>
					</fo:block>
				</fo:inline-container>
			</fo:block>
			<fo:block>
				<fo:inline-container inline-progression-dimension="49.9%" vertical-align="top">
					<fo:block>
						Compétition :
						<xsl:value-of select="competition"/>
					</fo:block>
				</fo:inline-container>
				<fo:inline-container inline-progression-dimension="49.9%" vertical-align="top">
					<fo:block>
						Poule :
						<xsl:value-of select="group"/>
					</fo:block>
				</fo:inline-container>
			</fo:block>
			<fo:block>
				<fo:inline-container inline-progression-dimension="33.3%" vertical-align="top">
					<fo:block>
						Date :
						<xsl:value-of select="date"/>
					</fo:block>
				</fo:inline-container>
				<fo:inline-container inline-progression-dimension="33.3%" vertical-align="top">
					<fo:block>
						Heure début :
						<xsl:value-of select="floor(beginTime div 60)"/>
						H
						<xsl:value-of select="beginTime mod 60"/>
					</fo:block>
				</fo:inline-container>
				<fo:inline-container inline-progression-dimension="33.3%" vertical-align="top">
					<fo:block>
						Heure Fin :
						<xsl:value-of select="floor(endTime div 60)"/>
						H
						<xsl:value-of select="endTime mod 60"/>
					</fo:block>
				</fo:inline-container>
			</fo:block>
		</fo:block>
	</xsl:template>
	
	<!-- Table of team names (at the right side of the page). -->
	<xsl:template name="teamNames">
		<fo:block>
			<fo:table table-layout="fixed" width="100%" text-align="center" vertical-align="middle">
				<fo:table-column column-width="16mm"/>
				<fo:table-column column-width="47mm"/>
				<fo:table-header xsl:use-attribute-sets="font-cell-header">
					<fo:table-row border="hidden">
						<fo:table-cell number-columns-spanned="2" xsl:use-attribute-sets="right-header-cell-decoration">
							<fo:block font-weight="bold">
								NOM DES EQUIPES
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-header>
				<fo:table-body xsl:use-attribute-sets="font-cell-body">
					<fo:table-row>
						<fo:table-cell text-align="left" xsl:use-attribute-sets="right-cell-decoration">
							<fo:block>
								Equipe A
							</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="left" xsl:use-attribute-sets="right-cell-decoration">
							<fo:block>
								<xsl:apply-templates select="/okronos/snapshots/snapshot[categoryId='team-right']/content/teamName"/>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration" text-align="left">
							<fo:block>
								Equipe B
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration" text-align="left">
							<fo:block>
								<xsl:apply-templates select="/okronos/snapshots/snapshot[categoryId='team-left']/content/teamName"/>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</fo:block>
	</xsl:template>
	
	<!-- Table of scores (at the right side of the page). -->
	<xsl:template name="score">
		<fo:block>
			<fo:block xsl:use-attribute-sets="font-title" text-align="center" space-before="05mm">
				SCORE FINAL
			</fo:block>
			<fo:table table-layout="fixed" width="100%" text-align="center" vertical-align="middle">
				<fo:table-column column-width="17mm"/>
				<fo:table-column column-width="23mm"/>
				<fo:table-column column-width="23mm"/>
				<fo:table-header xsl:use-attribute-sets="font-cell-header">
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="right-header-cell-decoration">
							<fo:block>
								&#160;
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="right-header-cell-decoration">
							<fo:block>
								Equipe A
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="right-header-cell-decoration">
							<fo:block>
								Equipe B
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-header>
				<fo:table-body xsl:use-attribute-sets="font-cell-body">
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block>
								1/2
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block>
								<xsl:value-of select="count(/okronos/snapshots/snapshot[categoryId='score-left']/content/marks/mark[period = 1])"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block>
								<xsl:value-of select="count(/okronos/snapshots/snapshot[categoryId='score-right']/content/marks/mark[period = 1])"/>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block>
								2/2
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block>
								<xsl:value-of select="count(/okronos/snapshots/snapshot[categoryId='score-left']/content/marks/mark[period = 2])"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block>
								<xsl:value-of select="count(/okronos/snapshots/snapshot[categoryId='score-right']/content/marks/mark[period = 2])"/>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration" number-columns-spanned="3">
							<fo:block>
								Après prolongations :
								<xsl:apply-templates select="/okronos/snapshots/snapshot[categoryId='match']/content/extension" mode="boolean"/>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block>
								Prolongation
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block>
								<xsl:value-of select="count(/okronos/snapshots/snapshot[categoryId='score-left']/content/marks/mark[period = 3])"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block>
								<xsl:value-of select="count(/okronos/snapshots/snapshot[categoryId='score-right']/content/marks/mark[period = 3])"/>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block>
								Tirs pénalité
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block>
								<xsl:value-of select="count(/okronos/snapshots/snapshot[categoryId='score-left']/content/marks/mark[period = 4])"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block>
								<xsl:value-of select="count(/okronos/snapshots/snapshot[categoryId='score-right']/content/marks/mark[period = 4])"/>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block>
								TOTAL
							</fo:block>
							<fo:block>
								(en chiffres)
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block padding-top="01mm">
								<xsl:value-of select="count(/okronos/snapshots/snapshot[categoryId='score-left']/content/marks/mark[period &lt; 5])"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block padding-top="01mm">
								<xsl:value-of select="count(/okronos/snapshots/snapshot[categoryId='score-right']/content/marks/mark[period &lt; 5])"/>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block>
								TOTAL
							</fo:block>
							<fo:block>
								(en lettres)
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block padding-top="01mm">
								<xsl:call-template name="in-lettres">
									<xsl:with-param name="value" select="count(/okronos/snapshots/snapshot[categoryId='score-left']/content/marks/mark[period &lt; 5])"/>
								</xsl:call-template>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block padding-top="01mm">
								<xsl:call-template name="in-lettres">
									<xsl:with-param name="value" select="count(/okronos/snapshots/snapshot[categoryId='score-right']/content/marks/mark[period &lt; 5])"/>
								</xsl:call-template>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</fo:block>
	</xsl:template>
	
	<!-- Table of aggregated team penalties (at the right side of the page). -->
	<xsl:template name="penalties-sumup">
		<fo:block>
			<fo:block xsl:use-attribute-sets="font-title" text-align="center" space-before="05mm">
				MINUTES DE PENALITES
			</fo:block>
			<fo:table table-layout="fixed" width="100%" text-align="center" vertical-align="middle">
				<fo:table-column column-width="17mm"/>
				<fo:table-column column-width="23mm"/>
				<fo:table-column column-width="23mm"/>
				<fo:table-header xsl:use-attribute-sets="font-cell-header">
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="right-header-cell-decoration">
							<fo:block>
								&#160;
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="right-header-cell-decoration">
							<fo:block>
								Equipe A
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="right-header-cell-decoration">
							<fo:block>
								Equipe B
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-header>
				<fo:table-body xsl:use-attribute-sets="font-cell-body">
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block>
								1/2
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block>
								<xsl:value-of select="count(/okronos/snapshots/snapshot[categoryId='penalty-left']/content/penalties/penalty[period = 1])"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block>
								<xsl:value-of select="count(/okronos/snapshots/snapshot[categoryId='penalty-right']/content/penalties/penalty[period = 1])"/>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block>
								2/2
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block>
								<xsl:value-of select="count(/okronos/snapshots/snapshot[categoryId='penalty-left']/content/penalties/penalty[period = 2])"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block>
								<xsl:value-of select="count(/okronos/snapshots/snapshot[categoryId='penalty-right']/content/penalties/penalty[period  = 2])"/>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block>
								TOTAL
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block>
								<xsl:value-of select="count(/okronos/snapshots/snapshot[categoryId='penalty-left']/content/penalties/penalty[period = 1 or period = 2])"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block>
								<xsl:value-of select="count(/okronos/snapshots/snapshot[categoryId='penalty-right']/content/penalties/penalty[period = 1 or period = 2])"/>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</fo:block>
	</xsl:template>
	
	<!-- Table of officials (at the right side of the page). -->
	<xsl:template name="officials">
		<fo:block>
			<fo:block xsl:use-attribute-sets="font-title" text-align="center" space-before="05mm">
				ARBITRES ET OFFICIELS
			</fo:block>
			<fo:table table-layout="fixed" width="100%" text-align="center" vertical-align="middle">
				<fo:table-column column-width="16mm"/>
				<fo:table-column column-width="15mm"/>
				<fo:table-column column-width="15mm"/>
				<fo:table-column column-width="17mm"/>
				<fo:table-header xsl:use-attribute-sets="font-cell-header">
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="right-header-cell-decoration">
							<fo:block>
								&#160;
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="right-header-cell-decoration">
							<fo:block>
								N° licence
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="right-header-cell-decoration">
							<fo:block>
								Nom
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="right-header-cell-decoration">
							<fo:block>
								Signature
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-header>
				<fo:table-body xsl:use-attribute-sets="font-cell-body">
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block padding-top="01mm">
								Arbitre 1
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block padding-top="01mm">
								<xsl:apply-templates select="/okronos/snapshots/snapshot[categoryId='match']/content/referee1/licence"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block padding-top="01mm">
								<xsl:apply-templates select="/okronos/snapshots/snapshot[categoryId='match']/content/referee1/name"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block>
								&#160;
							</fo:block>
							<fo:block>
								&#160;
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block padding-top="01mm">
								Arbitre 2
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block padding-top="01mm">
								<xsl:apply-templates select="/okronos/snapshots/snapshot[categoryId='match']/content/referee2/licence"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block padding-top="01mm">
								<xsl:apply-templates select="/okronos/snapshots/snapshot[categoryId='match']/content/referee2/name"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block>
								&#160;
							</fo:block>
							<fo:block>
								&#160;
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block padding-top="01mm">
								Marqueur
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block padding-top="01mm">
								<xsl:apply-templates select="/okronos/snapshots/snapshot[categoryId='match']/content/marker/licence"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block padding-top="01mm">
								<xsl:apply-templates select="/okronos/snapshots/snapshot[categoryId='match']/content/marker/name"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block>
								&#160;
							</fo:block>
							<fo:block>
								&#160;
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block padding-top="01mm">
								Chrono
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block padding-top="01mm">
								<xsl:apply-templates select="/okronos/snapshots/snapshot[categoryId='match']/content/chrono/licence"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block padding-top="01mm">
								<xsl:apply-templates select="/okronos/snapshots/snapshot[categoryId='match']/content/chrono/name"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
							<fo:block>
								&#160;
							</fo:block>
							<fo:block>
								&#160;
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</fo:block>
	</xsl:template>
	
	<!-- Logo. -->
	<xsl:template name="logo">
		<fo:block space-before="02mm" margin-left="18mm">
			<fo:external-graphic src="url('ffrsLogoBW.png')" content-height="25mm" content-width="25mm"/>
		</fo:block>
	</xsl:template>
	
	<xsl:template name="remarks">
		<fo:block>
			<fo:block xsl:use-attribute-sets="font-title" text-align="center" space-before="02mm">
			</fo:block>
			<fo:table table-layout="fixed" width="100%" text-align="center" border="hidden" vertical-align="middle">
				<fo:table-column column-width="46mm"/>
				<fo:table-column column-width="17mm"/>
				<fo:table-header xsl:use-attribute-sets="font-cell-header">
					<fo:table-row>
						<fo:table-cell number-columns-spanned="2" xsl:use-attribute-sets="right-header-cell-decoration">
							<fo:block>
								REMARQUES
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-header>
				<fo:table-body xsl:use-attribute-sets="font-remarques">
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="right-remarks-cell-decoration" text-align="left">
							<fo:block>
								Réserve d’avant match
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="right-remarks-cell-decoration">
							<fo:block>
								<xsl:apply-templates select="/okronos/snapshots/snapshot[categoryId='match']/content/reservesBeforeMatch" mode="boolean"/>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="right-remarks-cell-decoration" text-align="left">
							<fo:block>
								Réclamation
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets="right-remarks-cell-decoration">
							<fo:block>
								<xsl:apply-templates select="/okronos/snapshots/snapshot[categoryId='match']/content/claim" mode="boolean"/>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="right-remarks-cell-decoration" text-align="left">
							<fo:block>
								Rapport d’incident
							</fo:block>
						</fo:table-cell>
						<fo:table-cell border="hidden">
							<fo:block>
								<xsl:apply-templates select="/okronos/snapshots/snapshot[categoryId='match']/content/incidentReport" mode="boolean"/>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</fo:block>
	</xsl:template>
	
	<!-- Display a number only if it is strictly positive. -->
	<xsl:template match="text()" mode="positiveInteger">
		<xsl:variable name="value" select="."/>
		<xsl:if test="$value &gt; 0">
			<xsl:value-of select="$value"/>
		</xsl:if>
	</xsl:template>
	
	<!-- A cell content. Display a number only if it is strictly positive, otherwise Grey the cell.
	     Must be called into a table cell.
	 -->
	<xsl:template match="text()" mode="positiveContent">
		<xsl:variable name="value" select="."/>
		<xsl:if test="$value &lt;= 0">			
			<xsl:attribute name="background-color">
   				<xsl:value-of select="'#EEEEEE'"/>
			</xsl:attribute>				
		</xsl:if>
		<fo:block>
			<xsl:if test="$value &gt; 0">
				<xsl:value-of select="$value"/>
			</xsl:if>
		</fo:block>
	</xsl:template>
	
	<!-- A cell content. Display the content if not empty, otherwise Grey the cell.
	     Must be called into a table cell.
	 -->
	<xsl:template match="text()" mode="nonEmptyContent">
		<xsl:variable name="value" select="normalize-space(.)"/>
		<xsl:if test="$value = ''">			
			<xsl:attribute name="background-color">
   				<xsl:value-of select="'#EEEEEE'"/>
			</xsl:attribute>				
		</xsl:if>
		<fo:block>
			<xsl:if test="$value != ''">
				<xsl:value-of select="$value"/>
			</xsl:if>
		</fo:block>
	</xsl:template>
	
		<!-- A cell content. Display a time only if it is strictly positive, otherwise Grey the cell.
	     Must be called into a table cell.
	 -->
	<xsl:template match="text()" mode="timeContent">
		<xsl:variable name="value" select="."/>
		<xsl:if test="$value &lt;= 0">			
			<xsl:attribute name="background-color">#EEEEEE</xsl:attribute>				
		</xsl:if>		
		<fo:block>			
			<xsl:apply-templates select="." mode="time"/>
		</fo:block>
	</xsl:template>
	
	<xsl:template match="text()" mode="time">
		<xsl:variable name="value" select="."/>
		<xsl:variable name="min" select="floor($value div 60)"/>
		<xsl:variable name="sec" select="$value mod 60"/>
		<xsl:if test="$value &gt; 0">
			<xsl:if test="$min &lt; 10">
				<xsl:text>0</xsl:text>
			</xsl:if>
			<xsl:value-of select="$min"/>
			<xsl:text>:</xsl:text>
			<xsl:if test="$sec &lt; 10">
				<xsl:text>0</xsl:text>
			</xsl:if>
			<xsl:value-of select="$sec"/>
		</xsl:if>
	</xsl:template>
	
	<!-- Display a boolean of 'OUI' or 'NON'. -->
	<xsl:template match="text()" mode="boolean">
		<xsl:variable name="value" select="."/>
		<xsl:if test="$value = 'true'">
			OUI
		</xsl:if>
		<xsl:if test="$value = 'false'">
			NON
		</xsl:if>
	</xsl:template>
	
	<!-- Display a number in letter from 0 to 19. Used to display a full number in letter. -->
	<xsl:template name="in-lettres-0-to-19">
		<xsl:param name="value"/>
		<xsl:choose>
			<xsl:when test="$value = 0">
				<xsl:text>zéro</xsl:text>
			</xsl:when>
			<xsl:when test="$value = 1">
				<xsl:text>un</xsl:text>
			</xsl:when>
			<xsl:when test="$value = 2">
				<xsl:text>deux</xsl:text>
			</xsl:when>
			<xsl:when test="$value = 3">
				<xsl:text>trois</xsl:text>
			</xsl:when>
			<xsl:when test="$value = 4">
				<xsl:text>quatre</xsl:text>
			</xsl:when>
			<xsl:when test="$value = 5">
				<xsl:text>cinq</xsl:text>
			</xsl:when>
			<xsl:when test="$value = 6">
				<xsl:text>six</xsl:text>
			</xsl:when>
			<xsl:when test="$value = 7">
				<xsl:text>sept</xsl:text>
			</xsl:when>
			<xsl:when test="$value = 8">
				<xsl:text>huit</xsl:text>
			</xsl:when>
			<xsl:when test="$value = 9">
				<xsl:text>neuf</xsl:text>
			</xsl:when>
			<xsl:when test="$value = 10">
				<xsl:text>dix</xsl:text>
			</xsl:when>
			<xsl:when test="$value = 11">
				<xsl:text>onze</xsl:text>
			</xsl:when>
			<xsl:when test="$value = 12">
				<xsl:text>douze</xsl:text>
			</xsl:when>
			<xsl:when test="$value = 13">
				<xsl:text>treize</xsl:text>
			</xsl:when>
			<xsl:when test="$value = 14">
				<xsl:text>quatorze</xsl:text>
			</xsl:when>
			<xsl:when test="$value = 15">
				<xsl:text>quinze</xsl:text>
			</xsl:when>
			<xsl:when test="$value = 16">
				<xsl:text>seize</xsl:text>
			</xsl:when>
			<xsl:when test="$value = 17">
				<xsl:text>dix-sept</xsl:text>
			</xsl:when>
			<xsl:when test="$value = 18">
				<xsl:text>dix-huit</xsl:text>
			</xsl:when>
			<xsl:when test="$value = 19">
				<xsl:text>dix-neuf</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				&#160;
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- Display a dozen in letter. Used to display a full number in letter. -->
	<xsl:template name="dozen">
		<xsl:param name="value"/>
		<xsl:choose>
			<xsl:when test="$value = 1">
				<xsl:text>dix</xsl:text>
			</xsl:when>
			<xsl:when test="$value = 2">
				<xsl:text>vingt</xsl:text>
			</xsl:when>
			<xsl:when test="$value = 3">
				<xsl:text>trente</xsl:text>
			</xsl:when>
			<xsl:when test="$value = 4">
				<xsl:text>quarante</xsl:text>
			</xsl:when>
			<xsl:when test="$value = 5">
				<xsl:text>cinquante</xsl:text>
			</xsl:when>
			<xsl:when test="$value = 6">
				<xsl:text>soixante</xsl:text>
			</xsl:when>
			<xsl:when test="$value = 7">
				<xsl:text>soixante-dix</xsl:text>
			</xsl:when>
			<xsl:when test="$value = 8">
				<xsl:text>quatre-vingt</xsl:text>
			</xsl:when>
			<xsl:when test="$value = 9">
				<xsl:text>quatre-vingt-dix</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				&#160;
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- Display a number in letter. -->
	<xsl:template name="in-lettres">
		<xsl:param name="value"/>
		<xsl:variable name="unit-value" select="$value mod 10"/>
		<xsl:variable name="dozen-value" select="floor($value div 10)"/>
		<xsl:choose>
			<xsl:when test="$value &lt; 20">
				<xsl:call-template name="in-lettres-0-to-19">
					<xsl:with-param name="value" select="$value"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$value &lt; 100">
				<xsl:call-template name="dozen">
					<xsl:with-param name="value" select="$dozen-value"/>
				</xsl:call-template>
				<xsl:text>-</xsl:text>
				<xsl:if test="$unit-value = 1">
					<xsl:text>et-</xsl:text>
				</xsl:if>
				<xsl:if test="$unit-value != 0">
					<xsl:call-template name="in-lettres-0-to-19">
						<xsl:with-param name="value" select="$unit-value"/>
					</xsl:call-template>
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				&#160;
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- Display the position of a official (from A to F). -->
	<xsl:template name="official-position">
		<xsl:param name="value"/>
		<xsl:choose>
			<xsl:when test="$value = 1">
				<xsl:text>A</xsl:text>
			</xsl:when>
			<xsl:when test="$value = 2">
				<xsl:text>B</xsl:text>
			</xsl:when>
			<xsl:when test="$value = 3">
				<xsl:text>C</xsl:text>
			</xsl:when>
			<xsl:when test="$value = 4">
				<xsl:text>D</xsl:text>
			</xsl:when>
			<xsl:when test="$value = 5">
				<xsl:text>E</xsl:text>
			</xsl:when>
			<xsl:when test="$value = 6">
				<xsl:text>F</xsl:text>
			</xsl:when>			
			<xsl:otherwise>
				&#160;
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- The annex. -->
	<xsl:template name="annex">
		<fo:block>
			<xsl:call-template name="annex-top"/>
			<xsl:call-template name="annex-bottom"/>
		</fo:block>		
	</xsl:template>
	
	<!-- The top of the annex. -->
	<xsl:template name="annex-top">
		<fo:block space-after="2mm">
			<fo:inline-container inline-progression-dimension="262mm" vertical-align="top">
				<xsl:call-template name="annex-top-left"/>
			</fo:inline-container>
			<fo:inline-container inline-progression-dimension="28mm" vertical-align="top">
				<xsl:call-template name="annexe-logo"/>
			</fo:inline-container>
		</fo:block>
	</xsl:template>

	<!-- The top left part of the annex. -->
	<xsl:template name="annex-top-left">
		<fo:block>
			<fo:block xsl:use-attribute-sets="font-annexe-title" text-align="center">
				ANNEXE DE FEUILLE DE MATCH ROLLER HOCKEY
			</fo:block>
			<fo:block xsl:use-attribute-sets="font-annexe-title" text-align="center" space-after="5mm">
				réserves / réclamations / observations
			</fo:block>
				<xsl:apply-templates select="/okronos/snapshots/snapshot[categoryId='match']/content" mode="annex"/>
		</fo:block>
	</xsl:template>
	
	<!-- The match related data of the annex. -->
	<xsl:template match="//snapshot[categoryId='match']/content" mode="annex">
		<fo:block font-weight="bold" xsl:use-attribute-sets="font-annexe-header" text-align="left" >
			<fo:table table-layout="fixed" width="100%" 
				vertical-align="middle">
				<fo:table-column column-width="31mm"/>				
				<fo:table-column column-width="17mm"/>				
				<fo:table-column column-width="20mm"/>				
				<fo:table-column column-width="29mm"/>			
				<fo:table-column column-width="83mm"/>			
				<fo:table-column column-width="83mm"/>
				<fo:table-body>
					<fo:table-row>
						<fo:table-cell border="hidden">
							<fo:block text-align="left" margin-top="0.1mm">
								N° match :
								<xsl:value-of select="matchNumber"/>
							</fo:block>
						</fo:table-cell>						
						<fo:table-cell border="hidden" number-columns-spanned="3">
							<fo:block text-align="left" margin-top="0.1mm">
								Lieu :
								<xsl:value-of select="location"/>
							</fo:block>
						</fo:table-cell>						
						<fo:table-cell border="hidden">
							<fo:block text-align="left" margin-top="0.1mm">
									Nom du club A :
									<xsl:apply-templates select="/okronos/snapshots/snapshot[categoryId='team-left']/content/teamName"/>
							</fo:block>
						</fo:table-cell>						
						<fo:table-cell border="hidden">
							<fo:block text-align="left" margin-top="0.1mm">
									Nom de l’arbitre 1 :
									<xsl:apply-templates select="/okronos/snapshots/snapshot[categoryId='match']/content/referee1/name" />
							</fo:block>
						</fo:table-cell>						
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell border="hidden" number-columns-spanned="3">
							<fo:block text-align="left" margin-top="0.1mm">
								Compétition :
								<xsl:value-of select="competition"/>
							</fo:block>
						</fo:table-cell>						
						<fo:table-cell border="hidden">
							<fo:block text-align="left" margin-top="0.1mm">
								Poule :
								<xsl:value-of select="group"/>
							</fo:block>
						</fo:table-cell>						
						<fo:table-cell border="hidden">
							<fo:block text-align="left" margin-top="0.1mm">
								&#160;
							</fo:block>
						</fo:table-cell>						
						<fo:table-cell border="hidden">
							<fo:block text-align="left" margin-top="0.1mm">
								&#160;
							</fo:block>
						</fo:table-cell>						
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell border="hidden" number-columns-spanned="2">
							<fo:block text-align="left" margin-top="0.1mm">
								Date :
								<xsl:value-of select="date"/>
							</fo:block>
						</fo:table-cell>						
						<fo:table-cell border="hidden" number-columns-spanned="2">
							<fo:block text-align="left" margin-top="0.1mm">
								Heure :
								<xsl:value-of select="floor(beginTime div 60)"/>
								H
								<xsl:value-of select="beginTime mod 60"/>
							</fo:block>
						</fo:table-cell>						
						<fo:table-cell border="hidden">
							<fo:block text-align="left" margin-top="0.1mm">
								Nom du club B :
								<xsl:apply-templates select="/okronos/snapshots/snapshot[categoryId='team-right']/content/teamName"/>
							</fo:block>
						</fo:table-cell>						
						<fo:table-cell border="hidden">
							<fo:block text-align="left" margin-top="0.1mm">
								Nom de l’arbitre 2 :
								<xsl:apply-templates select="/okronos/snapshots/snapshot[categoryId='match']/content/referee2/name" />
							</fo:block>
						</fo:table-cell>						
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</fo:block>
	</xsl:template>
		
	<!-- The logo into the annex. -->
	<xsl:template name="annexe-logo">
		<fo:block>
			<fo:external-graphic src="url('ffrsLogoBW.png')" content-height="28mm" content-width="28mm"/>
		</fo:block>
	</xsl:template>
	
	<!-- The bottom of the annex : reserves and claims. -->
	<xsl:template name="annex-bottom">
		<fo:block>
			<fo:inline-container inline-progression-dimension="97mm" vertical-align="top">
				<xsl:call-template name="reserves"/>
			</fo:inline-container>
			<fo:inline-container inline-progression-dimension="190mm" vertical-align="top">
				<xsl:call-template name="annexe-bottom-right"/>
			</fo:inline-container>
		</fo:block>
	</xsl:template>
	
	<!-- The table of reserves. -->
	<xsl:template name="reserves">
		<fo:block>
			<fo:table table-layout="fixed" width="100%" 
				text-align="center" vertical-align="middle">
				<fo:table-column column-width="23.5mm"/>				
				<fo:table-column column-width="23.5mm"/>				
				<fo:table-column column-width="23.5mm"/>				
				<fo:table-column column-width="23.5mm"/>			
				<fo:table-header xsl:use-attribute-sets="font-cell-header">
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="annexe-header-decoration" number-columns-spanned="4">
							<fo:block>
								RESERVES D’AVANT-MATCH
							</fo:block>
						</fo:table-cell>						
					</fo:table-row>
				</fo:table-header>
				<fo:table-body xsl:use-attribute-sets="font-cell-body">
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration" height ="124mm" number-columns-spanned="4">
							<fo:block>								
							</fo:block>
						</fo:table-cell>						
					</fo:table-row>
					<xsl:call-template name="annexe-signatures"/>
				</fo:table-body>
			</fo:table>		
		</fo:block>
	</xsl:template>
	
	<!-- The bottom right of the annex : claims and observations. -->
	<xsl:template name="annexe-bottom-right">
		<fo:block>
			<xsl:call-template name="claims-and-observations">
				<xsl:with-param name="title" select="'RECLAMATIONS'"/>
			</xsl:call-template>
		</fo:block>
		<fo:block space-before="8mm">
			<xsl:call-template name="claims-and-observations">
				<xsl:with-param name="title" select="'OBSERVATIONS D’APRES-MATCH'"/>
			</xsl:call-template>
		</fo:block>
	</xsl:template>	
	
	<!-- The signature parts into a tables of the annex. -->
	<xsl:template name="annexe-signatures">
		<fo:table-row height="5mm"  xsl:use-attribute-sets="font-cell-header">
			<fo:table-cell xsl:use-attribute-sets="annexe-header-decoration" number-columns-spanned="2">
				<fo:block>
					Signature des capitaines ou des dirigants (officiels d’équipes)
				</fo:block>
			</fo:table-cell>						
			<fo:table-cell xsl:use-attribute-sets="annexe-header-decoration" number-columns-spanned="2">
				<fo:block>
					Signature des arbitres
				</fo:block>
			</fo:table-cell>						
		</fo:table-row>
		<fo:table-row xsl:use-attribute-sets="font-cell-header">
			<fo:table-cell xsl:use-attribute-sets="annexe-header-decoration">
				<fo:block>
					Club A
				</fo:block>
			</fo:table-cell>						
			<fo:table-cell xsl:use-attribute-sets="annexe-header-decoration">
				<fo:block>
					Club B
				</fo:block>
			</fo:table-cell>						
			<fo:table-cell xsl:use-attribute-sets="annexe-header-decoration">
				<fo:block>
					Arbitre 1
				</fo:block>
			</fo:table-cell>						
			<fo:table-cell xsl:use-attribute-sets="annexe-header-decoration">
				<fo:block>
					Arbitre 2
				</fo:block>
			</fo:table-cell>						
		</fo:table-row>
		<fo:table-row height ="20mm">
			<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
				<fo:block>					
				</fo:block>
			</fo:table-cell>						
			<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
				<fo:block>					
				</fo:block>
			</fo:table-cell>						
			<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
				<fo:block>					
				</fo:block>
			</fo:table-cell>						
			<fo:table-cell xsl:use-attribute-sets="right-cell-decoration">
				<fo:block>					
				</fo:block>
			</fo:table-cell>						
		</fo:table-row>
	</xsl:template>
	
	<!-- A table of claim or observation into the annex. -->
	<xsl:template name="claims-and-observations">
		<xsl:param name="title"/>
		<fo:block>
			<fo:table table-layout="fixed" width="100%" 
				text-align="center" vertical-align="middle">
				<fo:table-column column-width="48mm"/>				
				<fo:table-column column-width="48mm"/>				
				<fo:table-column column-width="48mm"/>				
				<fo:table-column column-width="48mm"/>				
				<fo:table-header xsl:use-attribute-sets="font-cell-header">
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="annexe-header-decoration" number-columns-spanned="4">
							<fo:block>
								<xsl:value-of select="$title"/>
							</fo:block>
						</fo:table-cell>						
					</fo:table-row>
				</fo:table-header>
				<fo:table-body xsl:use-attribute-sets="font-cell-body">
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets="right-cell-decoration" height ="38mm" number-columns-spanned="4">
							<fo:block>
							</fo:block>
						</fo:table-cell>						
					</fo:table-row>
					<xsl:call-template name="annexe-signatures"/>
				</fo:table-body>
			</fo:table>
		</fo:block>
	</xsl:template>

</xsl:stylesheet>
