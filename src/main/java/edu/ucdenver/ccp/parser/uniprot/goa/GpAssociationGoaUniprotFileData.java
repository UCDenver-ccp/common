package edu.ucdenver.ccp.parser.uniprot.goa;

import edu.ucdenver.ccp.parser.LineFileData;

/**
 * <pre>
 * This file currently consists of 11 tab-separated fields; their contents are described below.
 * 
 * 1. DB
 * Database from which annotated entry has been taken.
 * This file will contain one of either: UniProtKB or IPI
 * This field equates to column 1 in the GAF2.0 format.
 * 
 * 2. DB_Object_ID
 * A unique identifier in the database for the item being annotated.
 * Here: an accession number or identifier of the annotated protein
 * - a UniProtKB accession number or IPI identifier
 * Example: O00165
 * This field equates to column 2 in the GAF2.0 format.
 * 
 * 3. Qualifier
 * This column is used for flags that modify the interpretation of an annotation. If not 
 * null, then values in this field can equal: NOT, colocalizes_with, contributes_to, 
 * NOT | contributes_to, NOT | colocalizes_with
 * Example: NOT
 * This field equates to column 4 of the GAF2.0 format
 * 
 * 4. GO ID
 * The GO identifier for the term attributed to the DB_Object_ID.
 * Example: GO:0005634 
 * This field equates to column 5 of the GAF2.0 format
 * 
 * 5. DB:Reference
 * A single reference cited to support an annotation. Where an annotation cannot reference 
 * a paper, this field will contain a GO_REF identifier. 
 * See: http://www.geneontology.org/doc/GO.references for an explanation of the reference types 
 * used.
 * Examples:
 * PMID:9058808 DOI:10.1046/j.1469-8137.2001.00150.x GO_REF:0000002 
 * GO_REF:0000020 GO_REF:0000004 GO_REF:0000003 GO_REF:0000019 GO_REF:0000023 
 * GO_REF:0000024 
 * This field equates to column 6 in the GAF2.0 format.
 * 
 * 6. Evidence code
 * One of either EXP, IMP, IC, IGI, IPI, ISS, IDA, IEP, IEA, TAS, NAS, NR, ND or RCA.
 * Example: TAS 
 * This field equates to column 7 in the GAF2.0 format.
 * 
 * 7. With
 * An additional identifier to support annotations using certain evidence codes 
 * (including IEA, IPI, IGI, IC and ISS evidences).
 * Examples: UniProtKB:O00341 InterPro:IPROO1878 Ensembl:ENSG00000136141 GO:0000001 EC:3.1.22.1 
 * This field equates to column 8 in the GAF2.0 format
 * 
 * 8. Extra taxon ID
 * This taxon id should inform on the other organism involved in an multi-species interaction.
 * An extra taxon id can only be used in conjunction with terms that have the biological 
 * process term 'GO:0051704; multi-organism process' or the cellular component term 
 * 'GO:0043657; host cell' as an ancestor. This taxon id should inform on the other organism 
 * involved in the interaction. For further information please see: 
 * http://www.geneontology.org/GO.annotation.conventions.shtml#interactions
 * This field has been separated from the dual taxon id format allowed in the taxon (column 13 in GAF2.0 format).
 * 
 * 9. Date
 * The date of last annotation update in the format 'YYYYMMDD'
 * Example: 20050101 
 * This field equates to column 14 in the GAF2.0 format.
 * 
 * 10. Assigned_by
 * Attribute describing the source of the annotation.
 * One of either UniProtKB, AgBase, BHF-UCL, CGD, DictyBase, EcoCyc, EcoWiki, Ensembl, FB, GDB, GeneDB, GR 
 * (Gramene), HGNC, Human Protein Atlas, LIFEdb, MGI, PAMGO_GAT, Reactome, RGD, Roslin Institute, 
 * SGD, TAIR, TIGR, ZFIN, IntAct, PINC (Proteome Inc.) or WormBase.
 * Example: UniProtKB
 * This field equates to column 15 in the GAF2.0 format. 
 * 
 * 11. Annotation Extension
 * (N.B. Until annotation practices are finalised, this column will remain empty) Contains cross 
 * references to other ontologies/databases that can be used to qualify or enhance the annotation. 
 * The cross-reference is prefaced by an appropriate GO relationship; references to multiple ontologies can 
 * be entered.
 * Example: part_of(CL:0000084) occurs_in(GO:0009536) has_input(CHEBI:15422) 
 * has_output(CHEBI:16761) has_participant(UniProtKB:Q08722) 
 * part_of(CL:0000017)|part_of(MA:0000415)
 * This field equates to column 16 in the GAF2.0 format.
 * 
 * 12. Gene_Product_Form_ID
 * The fully-qualified unique identifier of a specific spliceform of the protein described in column 2 
 * (DB_Object_ID)
 * Example:UniProtKB:O43526-1
 * This field equates to column 17 in the GAF2.0 format.
 * 
 * </pre>
 * 
 * @author bill
 * 
 */
public class GpAssociationGoaUniprotFileData implements LineFileData {
	private final String database;
	private final String databaseObjectID;
	private final String qualifier;
	private final String goID;
	private final String dbReference;
	private final String evidenceCode;
	private final String with;
	private final String extraTaxonID;
	private final String date;
	private final String assignedBy;
	private final String annotationExtension;
	private final String geneProductFormID;

	public GpAssociationGoaUniprotFileData(String database, String databaseObjectID, String qualifier, String goID,
			String dbReference, String evidenceCode, String with, String extraTaxonID, String date, String assignedBy,
			String annotationExtension, String geneProductFormID) {
		super();
		this.database = database;
		this.databaseObjectID = databaseObjectID;
		this.qualifier = qualifier;
		this.goID = goID;
		this.dbReference = dbReference;
		this.evidenceCode = evidenceCode;
		this.with = with;
		this.extraTaxonID = extraTaxonID;
		this.date = date;
		this.assignedBy = assignedBy;
		this.annotationExtension = annotationExtension;
		this.geneProductFormID = geneProductFormID;
	}

	public String getDatabase() {
		return database;
	}

	public String getDatabaseObjectID() {
		return databaseObjectID;
	}

	public String getQualifier() {
		return qualifier;
	}

	public String getGoID() {
		return goID;
	}

	public String getDbReference() {
		return dbReference;
	}

	public String getEvidenceCode() {
		return evidenceCode;
	}

	public String getWith() {
		return with;
	}

	public String getExtraTaxonID() {
		return extraTaxonID;
	}

	public String getDate() {
		return date;
	}

	public String getAssignedBy() {
		return assignedBy;
	}

	public String getAnnotationExtension() {
		return annotationExtension;
	}

	public String getGeneProductFormID() {
		return geneProductFormID;
	}

	public boolean hasPubmedReference() {
		return dbReference.contains("PMID");
	}

	public int getPubmedReferenceID() {
		if (hasPubmedReference()) {
			return Integer.parseInt(dbReference.substring(dbReference.indexOf(':')+1));
		}
		throw new IllegalStateException("Cannot return Pubmed ID reference because there is not one to return.");
	}
}
