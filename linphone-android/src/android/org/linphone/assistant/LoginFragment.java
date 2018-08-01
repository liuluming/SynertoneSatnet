package org.linphone.assistant;
/*
LoginFragment.java
Copyright (C) 2017  Belledonne Communications, Grenoble, France

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
import org.linphone.LinphoneActivity;
import org.linphone.LinphoneManager;
import org.linphone.R;
import org.linphone.core.LinphoneAddress.TransportType;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.litesuits.common.utils.NetUtils;

public class LoginFragment extends Fragment implements OnClickListener, TextWatcher {
	private EditText login, userid, password, domain, displayName;
	private RadioGroup transports;
	private Button apply;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.assistant_login, container, false);

		login = view.findViewById(R.id.assistant_username);
		login.setInputType(InputType.TYPE_CLASS_NUMBER);
		login.addTextChangedListener(this);
		displayName = view.findViewById(R.id.assistant_display_name);
		displayName.addTextChangedListener(this);
		userid = view.findViewById(R.id.assistant_userid);
		userid.addTextChangedListener(this);
		password = view.findViewById(R.id.assistant_password);
		password.addTextChangedListener(this);
		domain = view.findViewById(R.id.assistant_domain);
		domain.addTextChangedListener(this);
		transports = view.findViewById(R.id.assistant_transports);
		apply = view.findViewById(R.id.assistant_apply);
		apply.setEnabled(false);
		apply.setOnClickListener(this);

		return view;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();

		if (id == R.id.assistant_apply) {
			if (login.getText() == null || login.length() == 0 || password.getText() == null || password.length() == 0 || domain.getText() == null || domain.length() == 0) {
				Toast.makeText(getActivity(), getString(R.string.first_launch_no_login_password), Toast.LENGTH_LONG).show();
				return;
			}

			TransportType transport;
			if(transports.getCheckedRadioButtonId() == R.id.transport_udp){
				transport = TransportType.LinphoneTransportUdp;
			} else {
				if(transports.getCheckedRadioButtonId() == R.id.transport_tcp){
					transport = TransportType.LinphoneTransportTcp;
				} else {
					transport = TransportType.LinphoneTransportTls;
				}
			}

			if (domain.getText().toString().compareTo(getString(R.string.default_domain)) == 0) {
				AssistantActivity.instance().displayLoginLinphone(login.getText().toString(), password.getText().toString());
			} else {
				if(NetUtils.isConnected(getActivity())){
					if(LinphoneManager.getLc().isNetworkReachable()) {
						AssistantActivity.instance().genericLogIn(login.getText().toString(), userid.getText().toString(), password.getText().toString(), displayName.getText().toString(), null, domain.getText().toString(), transport);
					}else{

						LinphoneActivity.instance().displayCustomToast(getString(R.string.error_network_unreachable), Toast.LENGTH_LONG);
					}
				}else{
					LinphoneActivity.instance().displayCustomToast(getString(R.string.error_network_unreachable), Toast.LENGTH_LONG);
				}

			}
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		apply.setEnabled(!login.getText().toString().isEmpty() && !password.getText().toString().isEmpty() && !domain.getText().toString().isEmpty());
	}

	@Override
	public void afterTextChanged(Editable s) {

	}
}
