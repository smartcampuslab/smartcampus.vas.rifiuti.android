/*******************************************************************************
 * Copyright 2012-2013 Trento RISE
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either   express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package eu.trentorise.smartcampus.rifiuti.model;

import java.io.Serializable;

/**
 * @author raman
 * 
 */
public class CalendarioItem implements Serializable {
	private static final long serialVersionUID = 517349652801258535L;

	private PuntoRaccolta point;
	private String color;
	private Calendario calendar;

	public PuntoRaccolta getPoint() {
		return point;
	}

	public void setPoint(PuntoRaccolta point) {
		this.point = point;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Calendario getCalendar() {
		return calendar;
	}

	public void setCalendar(Calendario calendar) {
		this.calendar = calendar;
	}
}
