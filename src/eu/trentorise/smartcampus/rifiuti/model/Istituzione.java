package eu.trentorise.smartcampus.rifiuti.model;


public class Istituzione {

	private String nome;
	private String tipologia;
	private String descrizione;
	private String ufficio;
	private String indirizzo;
	private String orarioUfficio;
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
	public String getUfficio() {
		return ufficio;
	}
	public void setUfficio(String ufficio) {
		this.ufficio = ufficio;
	}
	public String getIndirizzo() {
		return indirizzo;
	}
	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
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
	public String getOrarioUfficio() {
		return orarioUfficio;
	}
	public void setOrarioUfficio(String orarioUfficio) {
		this.orarioUfficio = orarioUfficio;
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
