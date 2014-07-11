package eu.trentorise.smartcampus.rifiuti.model;


public class DatiTipologiaRaccolta {

	private String tipologiaPuntoRaccolta;
	private String tipologiaRaccolta;
	private String colore;
	private String info;

	public String getTipologiaPuntoRaccolta() {
		return tipologiaPuntoRaccolta;
	}

	public void setTipologiaPuntoRaccolta(String tipologiaPuntoRaccolta) {
		this.tipologiaPuntoRaccolta = tipologiaPuntoRaccolta;
	}

	public String getTipologiaRaccolta() {
		return tipologiaRaccolta;
	}

	public void setTipologiaRaccolta(String tipologiaRaccolta) {
		this.tipologiaRaccolta = tipologiaRaccolta;
	}

	public String getColore() {
		return colore;
	}

	public void setColore(String colore) {
		this.colore = colore;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	@Override
	public String toString() {
		return "DatiTipologiaRaccolta [tipologiaPuntoRaccolta="
				+ tipologiaPuntoRaccolta + ", tipologiaRaccolta="
				+ tipologiaRaccolta + ", colore=" + colore + "]";
	}
}
