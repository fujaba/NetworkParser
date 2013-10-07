package de.uniks.jism.gui.table.controls;

/*
 Json Id Serialisierung Map
 Copyright (c) 2011 - 2013, Stefan Lindel
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 1. Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 3. All advertising materials mentioning features or use of this software
 must display the following acknowledgement:
 This product includes software developed by Stefan Lindel.
 4. Neither the name of contributors may be used to endorse or promote products
 derived from this software without specific prior written permission.

 THE SOFTWARE 'AS IS' IS PROVIDED BY STEFAN LINDEL ''AS IS'' AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL STEFAN LINDEL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

import java.text.ParseException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import de.uniks.jism.gui.table.celledit.EditField;
import de.uniks.jism.gui.table.celledit.EditFields;

public class SpinnerEditControl extends EditControl<Spinner> {
	private EditField owner;

	public SpinnerEditControl(EditField owner){
		this.fieldTyp = EditFields.INTEGER;
		this.owner = owner;
	}
	@Override
	public void setValue(Object value, boolean selectAll) {
		if (value instanceof Integer || value instanceof String) {
			control.setSelection(Integer.valueOf("" + value));
		}
	}

	@Override
	public boolean isCorrect(Object value, EditField editField)
			throws ParseException {
		if (value instanceof Number)
			return true;
		editField.convertFromString((String) value);
		return true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.keyCode == SWT.ESC) {
			e.doit = false;
			if(cellOwner != null){
				cellOwner.cancelEditor();
			}
		}
	}

	@Override
	public Object getEditorValue(boolean convert) throws ParseException {
		String value = control.getText();
		if(convert){
			return owner.convertFromString(value);
		}
		return value;
	}

	@Override
	public void createControl(EditField owner, Composite parent) {
		this.cellOwner = owner.getCellEditorElement();
		control = new Spinner(parent, SWT.BORDER);
		control.setMaximum(Integer.MAX_VALUE);
		control.setMinimum(Integer.MIN_VALUE);

		if(cellOwner!=null){
			control.addSelectionListener(cellOwner);
		};
	}
}
