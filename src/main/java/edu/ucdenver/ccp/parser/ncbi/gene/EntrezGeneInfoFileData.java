/*
 * Copyright (C) 2009 Center for Computational Pharmacology, University of Colorado School of Medicine
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 */
package edu.ucdenver.ccp.parser.ncbi.gene;

import java.util.Set;

import edu.ucdenver.ccp.parser.LineFileData;

/**
 * This class represents data contained in the EntrezGene gene_info file.
 * 
 * @author Bill Baumgartner
 * 
 */
public class EntrezGeneInfoFileData implements LineFileData {

	private final int taxonID;
	private final int geneID;
	private final String symbol;
	private final String locusTag;
	private final Set<String> synonyms;
	private final Set<String> dbXrefs;
	private final String chromosome;
	private final String mapLocation;
	private final String description;
	private final String typeOfGene;
	private final String symbolFromNomenclatureAuthority;
	private final String fullNameFromNomenclatureAuthority;
	private final String nomenclatureStatus;
	private final Set<String> otherDesignations;
	private final String modificationDate;

	public EntrezGeneInfoFileData(int taxonID, int geneID, String symbol, String locusTag, Set<String> synonyms, Set<String> dbXrefs,
            String chromosome, String mapLocation, String description, String typeOfGene, String symbolFromNomenclatureAuthority,
            String fullNameFromNomenclatureAuthority, String nomenclatureStatus, Set<String> otherDesignations, String modificationDate) {
        this.taxonID = taxonID;
        this.geneID = geneID;
        this.symbol = symbol;
        this.locusTag = locusTag;
        this.synonyms = synonyms;
        this.dbXrefs = dbXrefs;
        this.chromosome = chromosome;
        this.mapLocation = mapLocation;
        this.description = description;
        this.typeOfGene = typeOfGene;
        this.symbolFromNomenclatureAuthority = symbolFromNomenclatureAuthority;
        this.fullNameFromNomenclatureAuthority = fullNameFromNomenclatureAuthority;
        this.nomenclatureStatus = nomenclatureStatus;
        this.otherDesignations = otherDesignations;
        this.modificationDate = modificationDate;		
	}

	public int getTaxonID() {
		return taxonID;
	}

	public int getGeneID() {
		return geneID;
	}

	public String getSymbol() {
		return symbol;
	}

	public String getLocusTag() {
		return locusTag;
	}

	public Set<String> getSynonyms() {
		return synonyms;
	}

	public Set<String> getDbXrefs() {
		return dbXrefs;
	}

	public String getChromosome() {
		return chromosome;
	}

	public String getMapLocation() {
		return mapLocation;
	}

	public String getDescription() {
		return description;
	}

	public String getTypeOfGene() {
		return typeOfGene;
	}

	public String getSymbolFromNomenclatureAuthority() {
		return symbolFromNomenclatureAuthority;
	}

	public String getFullNameFromNomenclatureAuthority() {
		return fullNameFromNomenclatureAuthority;
	}

	public String getNomenclatureStatus() {
		return nomenclatureStatus;
	}

	public Set<String> getOtherDesignations() {
		return otherDesignations;
	}

	public String getModificationDate() {
		return modificationDate;
	}

}
