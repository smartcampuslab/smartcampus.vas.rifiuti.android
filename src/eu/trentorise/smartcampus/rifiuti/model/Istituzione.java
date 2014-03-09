package eu.trentorise.smartcampus.rifiuti.model;


public class Istituzione {

	private String nome;
	private String tipologia;
	private String descrizione;
	private String sitoIstituzionale;
	private String pec;
	private String email;
	private String telefono;
	private String fax;
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getTipologia() {
		return tipologia;
	}
	public void setTipologia(String tipologia) {
		this.tipologia = tipologia;
	}
	public String getDescrizione() {
		return descrizione;
	}
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	public String getSitoIstituzionale() {
		return sitoIstituzionale;
	}
	public void setSitoIstituzionale(String sitoIstituzionale) {
		this.sitoIstituzionale = sitoIstituzionale;
	}
	public String getPec() {
		return pec;
	}
	public void setPec(String pec) {
		this.pec = pec;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}

}
