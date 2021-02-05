package com.sIlence.androidracer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.*;
import android.widget.*;
import com.tapfortap.TapForTap;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

public class AndroidRacer extends Activity implements SoundPool.OnLoadCompleteListener {

	public final static String	DEFALT_SCREEN_NAME = "        No Named One";
	
    public static boolean		backgroundMusic = true;
    public static boolean		soundEffects = true;
    public static boolean		vibrateAlowed = true;

    private GameView			view;
    private Vibrator			v;

    private Typeface			font;

    private SoundPool			sound;
    private int					background = -1;

	
	private ArrayAdapter<String> adapter;
	private boolean				scaning = false;
	private HostThread			host;
	private ProgressDialog		hostDialog;
	private AlertDialog			multiplayerDialog;
	private ArrayList<String>	hostNames;
	private ArrayList<String>	hostIps;
	private String				screenName;
	
    private int					currentState = -1;

	public static int			STATE_OTHER = 0;
	public static int			STATE_MAIN_MENU = 1;
	public static int			STATE_SELECT_DIFFICUALTY = 2;
	public static int			STATE_HELP = 3;
	public static int			STATE_HIGH_SCORES = 4;
	public static int			STATE_MULTIPLAYER = 5;
	public static int			STATE_IN_GAME = 6;
	public static int			STATE_MULTIPLAYER_HOSTING = 7;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        currentState = STATE_OTHER;
        Object nonConfig = getLastNonConfigurationInstance();

        if (nonConfig != null) {
            Game g = (Game) nonConfig;

            view = new GameView(this, 0, g);

            currentState = g.currentState;
			screenName = g.screenName;
        }


		
        font = Typeface.createFromAsset(getAssets(), "bank_gothic.ttf");
		
	
		hostNames = new ArrayList<String>();
		hostIps = new ArrayList<String>();
		
		screenName = DEFALT_SCREEN_NAME;
    }

    public void menu() {
        currentState = STATE_MAIN_MENU;
	
		unlockOrientation();

        final Context context = this;

        setContentView(R.layout.main);


		
        TextView head = (TextView) findViewById(R.id.main_head);
        head.setTypeface(font, Typeface.NORMAL);

        Button resume = (Button) findViewById(R.id.resume);
        resume.setTypeface(font, Typeface.NORMAL);
        resume.setOnClickListener(new OnClickListener() {
            public void onClick(android.view.View arg0) {
                v.vibrate(25);
                if (view != null && view.game.local != null) {
                    sound.pause(background);
                    currentState = STATE_IN_GAME;
                    setContentView(view);
                } else {
                    Toast.makeText(context, "No resent game to play.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        Button play = (Button) findViewById(R.id.play);
        play.setTypeface(font, Typeface.NORMAL);
        play.setOnClickListener(new OnClickListener() {
            public void onClick(android.view.View arg0) {
                v.vibrate(25);
                selectDifficualty();
            }
        });

        Button multi = (Button) findViewById(R.id.multi);
        multi.setTypeface(font, Typeface.NORMAL);
		multi.setOnClickListener(new OnClickListener() {
			public void onClick(android.view.View arg0) {
				v.vibrate(25);
				multiplayer();
			}
		});
		
        Button help = (Button) findViewById(R.id.help_button);
        help.setTypeface(font, Typeface.NORMAL);
        help.setOnClickListener(new OnClickListener() {
            public void onClick(android.view.View arg0) {
                v.vibrate(25);
                help();
            }
        });

        Button highScore = (Button) findViewById(R.id.high_score_button);
        highScore.setTypeface(font, Typeface.NORMAL);
        highScore.setOnClickListener(new OnClickListener() {
            public void onClick(android.view.View arg0) {
                v.vibrate(25);
                highScores();
            }
        });

        Button settings = (Button) findViewById(R.id.settings_button);
        settings.setTypeface(font, Typeface.NORMAL);
        settings.setOnClickListener(new OnClickListener() {
            public void onClick(android.view.View arg0) {
                v.vibrate(25);

                Intent prefs = new Intent(getApplicationContext(), SettingsPreferences.class);
                startActivity(prefs);
            }
        });

        TextView footer = (TextView) findViewById(R.id.footer);
        footer.setTypeface(font, Typeface.NORMAL);


        sound.resume(background);
    }

    public void selectDifficualty() {
        currentState = STATE_SELECT_DIFFICUALTY;

        final Context context = this;
        setContentView(R.layout.difficualty);

		
        TextView head = (TextView) findViewById(R.id.play_head);
        head.setTypeface(font, Typeface.NORMAL);

        Button child = (Button) findViewById(R.id.diff_child);
        child.setTypeface(font, Typeface.NORMAL);
        child.setOnClickListener(new OnClickListener() {
            public void onClick(android.view.View arg0) {
				v.vibrate(25);
                newGame(new GameView(context, AIRacer.DIFF_CHILD, null));
            }
        });

        Button easy = (Button) findViewById(R.id.diff_easy);
        easy.setTypeface(font, Typeface.NORMAL);
        easy.setOnClickListener(new OnClickListener() {
            public void onClick(android.view.View arg0) {
				v.vibrate(25);
                newGame(new GameView(context, AIRacer.DIFF_EASY, null));
            }
        });

        Button medi = (Button) findViewById(R.id.diff_medium);
        medi.setTypeface(font, Typeface.NORMAL);
        medi.setOnClickListener(new OnClickListener() {
            public void onClick(android.view.View arg0) {
                v.vibrate(25);
                newGame(new GameView(context, AIRacer.DIFF_MEDI, null));
            }
        });

        Button hard = (Button) findViewById(R.id.diff_hard);
        hard.setTypeface(font, Typeface.NORMAL);
        hard.setOnClickListener(new OnClickListener() {
            public void onClick(android.view.View arg0) {
                v.vibrate(25);
                newGame(new GameView(context, AIRacer.DIFF_HARD, null));
			}
        });

        Button insa = (Button) findViewById(R.id.diff_insane);
        insa.setTypeface(font, Typeface.NORMAL);
        insa.setOnClickListener(new OnClickListener() {
            public void onClick(android.view.View arg0) {
                v.vibrate(25);
                newGame(new GameView(context, AIRacer.DIFF_INSANE, null));
            }
        });

        Button back = (Button) findViewById(R.id.back);
        back.setTypeface(font, Typeface.NORMAL);
        back.setOnClickListener(new OnClickListener() {
            public void onClick(android.view.View arg0) {
                v.vibrate(25);
                if (currentState == STATE_IN_GAME) {
                    setContentView(view);
                    sound.pause(background);
                }
                else menu();
            }
        });

        TextView footer = (TextView) findViewById(R.id.footer);
        footer.setTypeface(font, Typeface.NORMAL);
    }
	
	public void updateScreenName() {
		EditText nameView = (EditText) multiplayerDialog.findViewById(R.id.multiplayer_name_name);
		String n = nameView.getText().toString();
					
		if (n.length() == 0) {
			Toast.makeText(this, "Please Enter A Screen Name", Toast.LENGTH_SHORT).show();
			return;
		}
					
		screenName = "";
		int whiteSpace = 20 - n.length();
		for (int i = 0; i < whiteSpace; i++) {
			screenName += " ";
		}
					
		screenName += n;
		
		if (view != null) {
			view.game.screenName = screenName;
		}
		
		multiplayerDialog.dismiss();
	}
	
	public void getScreenName() {
		LayoutInflater factory = LayoutInflater.from(this);
		View textEntryView = factory.inflate(R.layout.multiplayer_screen_name, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder
		.setView(textEntryView)
		.setCancelable(true)
		.setOnCancelListener(new DialogInterface.OnCancelListener() {

			public void onCancel(DialogInterface dialog) {
				if (multiplayerDialog != null) multiplayerDialog.dismiss();
				menu();
			}
		});
		multiplayerDialog = builder.create();
		multiplayerDialog.show();
				
		TextView mHead = (TextView) multiplayerDialog.findViewById(R.id.multiplayer_name_title);
		mHead.setTypeface(font, Typeface.NORMAL);
			
		final EditText nameView = (EditText) multiplayerDialog.findViewById(R.id.multiplayer_name_name);
		nameView.setTypeface(font, Typeface.NORMAL);
		nameView.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
					updateScreenName();
					return true;
				}
				return false;
			}
		});
		if (!screenName.equals(DEFALT_SCREEN_NAME)) {
			nameView.setText(screenName.trim());
		}
			
		Button done = (Button) multiplayerDialog.findViewById(R.id.multiplayer_name_done);
		done.setTypeface(font, Typeface.NORMAL);
		done.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				updateScreenName();
			}
		});
	}
	
	public void multiplayer() {
        currentState = STATE_MULTIPLAYER;

		if (screenName.equals(DEFALT_SCREEN_NAME)) {
			getScreenName();
			scan();
		}
		
        setContentView(R.layout.multiplayer);
		

		
		final AndroidRacer an = this;
		
        TextView head = (TextView) findViewById(R.id.multi_head);
        head.setTypeface(font, Typeface.NORMAL);
		
		ListView hosts = (ListView) findViewById(R.id.host_list);
		adapter = new ArrayAdapter<String>(this, R.layout.host_item, hostNames);
		hosts.setAdapter(adapter);
		
		final Context c = this;
		
		hosts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				v.vibrate(25);
				
				multiplayerDialog = ProgressDialog.show(c, "Waiting For Other Player", "Wait or tell them to hurry up", true, true);
				
				final String name = adapter.getItem(position);
				String hostip = null;
				
				for (int i = 0; i < hostNames.size(); i++) {
					if (hostNames.get(i).equals(name)) {
						hostip = hostIps.get(i);
					}
				}
				
				
				final String ip = hostip;
				
				Thread connect = new Thread() {

					@Override
					public void run() {
						try {
							Socket sock = new Socket(ip, 4444);
					
							int isServer = sock.getInputStream().read();
					
							if (isServer != HostSockConnect.SERVER_CODE) {
								sock.close();
								throw new Exception(name + " (" + ip + ") is not a host");
							}
		
							sock.getOutputStream().write(HostSockConnect.PLAY);
							sock.getOutputStream().write(screenName.getBytes());
							
							int b = sock.getInputStream().read();
						
							Looper.prepare();
					
							if (b == HostSockConnect.ACCEPTIING_PLAY) {
								if (multiplayerDialog != null) multiplayerDialog.dismiss();
														
							    newGame(new MultiplayerGameView(c, null, sock));
								lockOrientation();
							} else if (b == HostSockConnect.DECLINING_PLAY) {
								sock.close();
								if (multiplayerDialog != null) multiplayerDialog.dismiss();
						
								runOnUiThread(new Runnable() {
									public void run() {
										AlertDialog.Builder builder = new AlertDialog.Builder(an);
										builder
										.setTitle("Host Declined")
										.setMessage("It would appear as you are not wanted")
										.setCancelable(true);
										multiplayerDialog = builder.create();
										multiplayerDialog.show();
									}
								});
							} else {
								throw new Exception("An unknown error occured");
							}
						}  catch (Exception e) {
							e.printStackTrace();
							
							if (multiplayerDialog != null) multiplayerDialog.dismiss();
						
							runOnUiThread(new Runnable() {
								public void run() {	
									AlertDialog.Builder builder = new AlertDialog.Builder(an);
									builder
									.setTitle("Error")
									.setMessage("An error occured while connecting")
									.setCancelable(true);
									multiplayerDialog = builder.create();
									multiplayerDialog.show();
								}
							});
						}
					}
				};
					
				connect.start();
				
			}
		});
		
        Button scan = (Button) findViewById(R.id.scan_multi);		
        scan.setTypeface(font, Typeface.NORMAL);
        scan.setOnClickListener(new OnClickListener() {
            public void onClick(android.view.View arg0) {
                v.vibrate(25);
				scan();
            }
        });
		
		Button newScreenName = (Button) findViewById(R.id.new_screen_name);		
        newScreenName.setTypeface(font, Typeface.NORMAL);
        newScreenName.setOnClickListener(new OnClickListener() {
            public void onClick(android.view.View arg0) {
                v.vibrate(25);
				
				getScreenName();
            }
        });

		final Button hostb = (Button) findViewById(R.id.multi_host);
		hostb.setTypeface(font, Typeface.NORMAL);
		hostb.setOnClickListener(new OnClickListener() {
			public void onClick(android.view.View arg0) {
				v.vibrate(25);
				
				host = new HostThread(an);
				host.start();
					
				currentState = STATE_MULTIPLAYER_HOSTING;
				
				
				hostDialog = ProgressDialog.show(c, "Hosting", "Waiting for an opponent to connect", true, true, new DialogInterface.OnCancelListener() {
					public void onCancel(DialogInterface dialog) {
						host.closeHost();
					}
				});
			}
		});
		
        Button back = (Button) findViewById(R.id.back);
        back.setTypeface(font, Typeface.NORMAL);
        back.setOnClickListener(new OnClickListener() {
            public void onClick(android.view.View arg0) {
                v.vibrate(25);
				
				if (host != null) host.closeHost();
				
				menu();
            }
        });

        TextView footer = (TextView) findViewById(R.id.footer);
        footer.setTypeface(font, Typeface.NORMAL);
    }
	 
	public void scan() {
		if (scaning) {
            Toast.makeText(this, "Already Scanning", Toast.LENGTH_SHORT).show();
			return;
		}
		
		ListView hosts = (ListView) findViewById(R.id.host_list);
		if (hosts == null) return;

		hostNames.clear();
		hostIps.clear();
		
		adapter.clear();
		
		final Context c = this;
		
		Thread scan = new Thread() {
			@Override
			public void run() {
				try {
					scaning = true;
				
					Socket sock = new Socket();
						
					String localFull = "192.168.1.1"; //= null; // Change to null
				
					Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
					for (NetworkInterface netint : Collections.list(nets)) {
						
						if (netint.getName().equals("wlan0")) {				
							Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
								
							for (InetAddress inetAddress : Collections.list(inetAddresses)) {
								localFull = inetAddress.getHostAddress();
							}
						}
					}
			
					if (localFull == null) {
						throw new Exception("Not connected to a network, you must connect to a network to play multiplayer");
					}
					
					String localDomain = "";
					int dotsFound = 0;
					for (int i = 0; i < localFull.length(); i++) {
						if (localFull.charAt(i) == '.') dotsFound++;
							
						localDomain += localFull.charAt(i);
							
						if (dotsFound == 3) {
							break;
						}
					}
					
						
					for (int i = 1; i < 255; i++) {
					
						final String ip = localDomain + i;
						
						if (ip.equals(localFull)) continue;
						
						try {
							sock.close();
							sock = new Socket();
							sock.connect(new InetSocketAddress(ip, 4444), 50);
						
							int b = sock.getInputStream().read();
						
							if (b == HostSockConnect.SERVER_CODE) {
								sock.getOutputStream().write(HostSockConnect.JUST_CHECKING);
								
								
								byte[] buffer = new byte[20];
								sock.getInputStream().read(buffer, 0, 20);
								final String name = new String(buffer);
								
								
							
								runOnUiThread(new Runnable() {
									public void run() {
										int j = hostNames.size();
										hostNames.add(j, name);
										hostIps.add(j, ip);
										
										Log.d("AndroidRacer", "name: " + name);
										Log.d("AndroidRacer", "ip  : " + ip);
										
										adapter.notifyDataSetChanged();
									}
								});
							}
						} catch (SocketTimeoutException e) {
						
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					scaning = false;
				} catch (final Exception e) {
					e.printStackTrace();
					
					if (multiplayerDialog != null) multiplayerDialog.dismiss();
					
					runOnUiThread(new Runnable() {
						public void run() {	
							AlertDialog.Builder builder = new AlertDialog.Builder(c);
							builder
							.setTitle("Error")
							.setMessage(e.getMessage())
							.setCancelable(true);
							multiplayerDialog = builder.create();
							multiplayerDialog.show();
							
							menu();
						}
					});
				}
			}
		};
		scan.start();
	}

    public void help() {
        currentState = STATE_HELP;

        setContentView(R.layout.help);


        TextView head = (TextView) findViewById(R.id.help_head);
        head.setTypeface(font, Typeface.NORMAL);

        EditText help = (EditText) findViewById(R.id.help_stuff);
        help.setTypeface(font, Typeface.NORMAL);

        Button back = (Button) findViewById(R.id.back);
        back.setTypeface(font, Typeface.NORMAL);
        back.setOnClickListener(new OnClickListener() {
            public void onClick(android.view.View arg0) {
                v.vibrate(25);
                if (currentState == STATE_IN_GAME) {
                    setContentView(view);
                    sound.pause(background);
                }
                else menu();
            }
        });

        TextView footer = (TextView) findViewById(R.id.footer);
        footer.setTypeface(font, Typeface.NORMAL);
    }

	public void highScores() {
		currentState = STATE_HIGH_SCORES;

		setContentView(R.layout.high_score);


		
		TextView head = (TextView) findViewById(R.id.high_scores_head);
		head.setTypeface(font, Typeface.NORMAL);

		((TextView) findViewById(R.id.type_head)).setTypeface(font);
		((TextView) findViewById(R.id.name_head)).setTypeface(font);
		((TextView) findViewById(R.id.score_head)).setTypeface(font);

		((TextView) findViewById(R.id.child_type)).setTypeface(font);
		((TextView) findViewById(R.id.child_name)).setTypeface(font);
		((TextView) findViewById(R.id.child_score)).setTypeface(font);


		((TextView) findViewById(R.id.easy_type)).setTypeface(font);
		((TextView) findViewById(R.id.easy_name)).setTypeface(font);
		((TextView) findViewById(R.id.easy_score)).setTypeface(font);


		((TextView) findViewById(R.id.medium_type)).setTypeface(font);
		((TextView) findViewById(R.id.medium_name)).setTypeface(font);
		((TextView) findViewById(R.id.medium_score)).setTypeface(font);


		((TextView) findViewById(R.id.hard_type)).setTypeface(font);
		((TextView) findViewById(R.id.hard_name)).setTypeface(font);
		((TextView) findViewById(R.id.hard_score)).setTypeface(font);


		((TextView) findViewById(R.id.insane_type)).setTypeface(font);
		((TextView) findViewById(R.id.insane_name)).setTypeface(font);
		((TextView) findViewById(R.id.insane_score)).setTypeface(font);

		Database db = new Database(this);
		db.readHighScores(this);

		Button back = (Button) findViewById(R.id.back);
		back.setTypeface(font, Typeface.NORMAL);
		back.setOnClickListener(new OnClickListener() {
			public void onClick(android.view.View arg0) {
				v.vibrate(25);
				menu();
			}
		});

		TextView footer = (TextView) findViewById(R.id.footer);
		footer.setTypeface(font, Typeface.NORMAL);
	}

	@Override
	public void onBackPressed() {
		if (currentState == STATE_MAIN_MENU) {
			if (view != null) view.stopGame();
			
			finish();
		} else if (view == null) {
			menu();
		} else if (!view.isPaused()) {
			view.pause();
		} else {
			view.stopGame();
			menu();
		}
	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		if (view != null && !view.isPaused()) view.pause();

		Intent prefsIntent = new Intent(getApplicationContext(), SettingsPreferences.class);

		MenuItem preferences = menu.findItem(R.id.settings);
		preferences.setIntent(prefsIntent);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU ) {
			if (view != null && !view.isPaused()) {
				view.pause();
			}
		}
		
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		v.vibrate(25);
		if (currentState == STATE_IN_GAME) view.pause();
		int itemId = item.getItemId();
		if (itemId == R.id.new_game) {
			selectDifficualty();

			return true;
		} else if (itemId == R.id.help) {
			help();

			return true;
		} else if (itemId == R.id.settings) {
			startActivity(item.getIntent());
			onStop();

			return true;
		}
		return super.onOptionsItemSelected(item);
	}



	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (view != null) {
			view.game.currentState = currentState;
		}
	}

	@Override
	protected void onStop() {
		sound.release();
		if (view != null) view.stopGame();

		super.onStop();
		onDestroy();
	}

	@Override
	protected void onPause() {
		if (view != null && !view.isPaused()) {
			view.pause();
		}
		sound.pause(background);
		
		if (host != null) {
			host.closeHost();
		}

		if (multiplayerDialog != null) multiplayerDialog.dismiss();
		if (hostDialog != null) hostDialog.dismiss();
		
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onStart() {
		super.onStart();

		// settings
		SharedPreferences settings = getSharedPreferences("AndroidRacerSettings", 0);
		backgroundMusic = settings.getBoolean("background", true);
		soundEffects = settings.getBoolean("effects", true);
		vibrateAlowed = settings.getBoolean("vibrate", true);


		sound = new SoundPool(2, AudioManager.STREAM_MUSIC, 1);
		sound.setOnLoadCompleteListener(this);
		background = sound.load(this, R.raw.menu_background, 9);


		if (view != null && view.game.currentState == STATE_IN_GAME) {
			sound.pause(background);
			setContentView(view);
		} else if (currentState == STATE_OTHER) {
			menu();
		} else if (currentState == STATE_MAIN_MENU) {
			menu();
		} else if (currentState == STATE_SELECT_DIFFICUALTY) {
			selectDifficualty();
		} else if (currentState == STATE_HELP) {
			help();
		} else if (currentState == STATE_HIGH_SCORES) {
			highScores();
		} else if (currentState == STATE_MULTIPLAYER) {
			multiplayer();
		} else if (currentState == STATE_MULTIPLAYER_HOSTING) {
			multiplayer();
	
			host = new HostThread(this);
			host.start();
			
			hostDialog = ProgressDialog.show(this, "Hosting", "Waiting for an opponent to connect", true, true, new DialogInterface.OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					host.closeHost();
				}
			});
			
			currentState = STATE_MULTIPLAYER_HOSTING;
		} else {
			menu();
		}
		
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		Game game;
		
		if (view != null) {
			game = view.game;
		} else {
			game = new Game();
		}
				
		game.currentState = currentState;
		game.screenName = screenName;
		
		return game;
	}

	public void onLoadComplete(SoundPool arg0, int arg1, int arg2) {
		if (backgroundMusic) sound.play(background, 0.5f, 0.5f, 0, -1, 1.0f);
		if (currentState == STATE_IN_GAME) sound.pause(background);
	}
	
	public void newGame(final GameView newView) {
		runOnUiThread(new Runnable() {
			public void run() {	
				if (hostDialog != null) hostDialog.dismiss();
				if (multiplayerDialog != null) multiplayerDialog.dismiss();
				if (host != null) host.closeHost();
				
				sound.pause(background);
			    view = newView;
			    setContentView(view);
			    currentState = STATE_IN_GAME;
			}
		});
	}
	
	public void lockOrientation() {
		int orientation = getResources().getConfiguration().orientation;
        int rotation = getWindowManager().getDefaultDisplay().getRotation();

        if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_90) {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }
        else if (rotation == Surface.ROTATION_180 || rotation == Surface.ROTATION_270) {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
            }
            else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            }
        }
	}
	
	public void unlockOrientation() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
	}
	
	public String screenName() {
		return screenName;
	}
}