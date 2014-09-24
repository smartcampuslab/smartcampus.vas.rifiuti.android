package eu.trentorise.smartcampus.rifiuti.model;

public class SysProfile {

	private String profilo;
	private String tipologiaUtenza;
	private String descrizione;

	public String getProfilo() {
		return profilo;
	}

	public void setProfilo(String profilo) {
		this.profilo = profilo;
	}

	public String getTipologiaUtenza() {
		return tipologiaUtenza;
	}

	public void setTipologiaUtenza(String tipologiaUtenza) {
		this.tipologiaUtenza = tipologiaUtenza;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@Override
	public String toString() {
		return "SysProfile [profilo=" + profilo + ", tipologiaUtenza=" + tipologiaUtenza + ", descrizione=" + descrizione + "]";
	}
}
