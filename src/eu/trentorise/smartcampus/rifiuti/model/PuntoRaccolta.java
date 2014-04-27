package eu.trentorise.smartcampus.rifiuti.model;

import java.io.Serializable;

import android.util.Log;

public class PuntoRaccolta implements Serializable {
	private static final long serialVersionUID = -2577147915158632199L;

	private String area;
	private String tipologiaPuntiRaccolta;
	private String tipologiaUtenza;
	private String localizzazione;
	private String indirizzo;
	private String dettaglioIndirizzo;

	private Boolean gettoniera;
	private Boolean residuo;
	private Boolean imbCarta;
	private Boolean imbPlMet;
	private Boolean organico;
	private Boolean imbVetro;
	private Boolean indumenti;
	private String note;

	private transient double[] location;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((indirizzo == null) ? 0 : indirizzo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PuntoRaccolta other = (PuntoRaccolta) obj;
		if (indirizzo == null) {
			if (other.indirizzo != null)
				return false;
		} else if (!indirizzo.equals(other.indirizzo))
			return false;
		return true;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getTipologiaPuntiRaccolta() {
		return tipologiaPuntiRaccolta;
	}

	public void setTipologiaPuntiRaccolta(String tipologiaPuntiRaccolta) {
		this.tipologiaPuntiRaccolta = tipologiaPuntiRaccolta;
	}

	public String getTipologiaUtenza() {
		return tipologiaUtenza;
	}

	public void setTipologiaUtenza(String tipologiaUtenza) {
		this.tipologiaUtenza = tipologiaUtenza;
	}

	public String getLocalizzazione() {
		return localizzazione;
	}

	public void setLocalizzazione(String localizzazione) {
		this.localizzazione = localizzazione;
	}

	public String getIndirizzo() {
		return indirizzo;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}
	public String getDettaglioIndirizzo() {
		return dettaglioIndirizzo;
	}

	public Boolean isGettoniera() {
		return gettoniera;
	}

	public void setGettoniera(Boolean gettoniera) {
		this.gettoniera = gettoniera;
	}

	public Boolean isResiduo() {
		return residuo;
	}

	public void setResiduo(Boolean residuo) {
		this.residuo = residuo;
	}

	public Boolean isImbCarta() {
		return imbCarta;
	}

	public void setImbCarta(Boolean imbCarta) {
		this.imbCarta = imbCarta;
	}

	public Boolean isImbPlMet() {
		return imbPlMet;
	}

	public void setImbPlMet(Boolean imbPlMet) {
		this.imbPlMet = imbPlMet;
	}

	public Boolean isOrganico() {
		return organico;
	}

	public void setOrganico(Boolean organico) {
		this.organico = organico;
	}

	public Boolean isImbVetro() {
		return imbVetro;
	}

	public void setImbVetro(Boolean imbVetro) {
		this.imbVetro = imbVetro;
	}

	public Boolean isIndumenti() {
		return indumenti;
	}

	public void setIndumenti(Boolean indumenti) {
		this.indumenti = indumenti;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public void setDettaglioIndirizzo(String dettaglioIndirizzo) {
		this.dettaglioIndirizzo = dettaglioIndirizzo;
	}

	public String dettaglio() {
		if (dettaglioIndirizzo != null && dettaglioIndirizzo.length() > 0) return dettaglioIndirizzo;
		return indirizzo;
	}
	
	public double[] location() {
		if (location == null) {
			try {
				String[] splittedLatLong = localizzazione.split(",");
				location = new double[]{Double.parseDouble(splittedLatLong[0]), Double.parseDouble(splittedLatLong[1])};
			} catch (Exception e) {
				Log.e("PuntoRaccolta", "error parsing location: "+e.getMessage());
			}
		}
		return location;
	}
	
	@Override
	public String toString() {
		return "PuntoRaccolta [area=" + area + ", tipologiaPuntiRaccolta=" + tipologiaPuntiRaccolta + ", tipologiaUtenza="
				+ tipologiaUtenza + ", localizzazione=" + localizzazione + ", indirizzo=" + indirizzo + "]";
	}

}
