package eu.trentorise.smartcampus.rifiuti.model;


public class PuntoRaccolta {
	
	private String area;
	private String tipologiaPuntiRaccolta;
	private String tipologiaUtenza;
	private String localizzazione;
	private String indirizzo;

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

	@Override
	public String toString() {
		return "PuntoRaccolta [area=" + area + ", tipologiaPuntiRaccolta="
				+ tipologiaPuntiRaccolta + ", tipologiaUtenza="
				+ tipologiaUtenza + ", localizzazione=" + localizzazione
				+ ", indirizzo=" + indirizzo + "]";
	}
}
