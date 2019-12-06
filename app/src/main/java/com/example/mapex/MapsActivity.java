package com.example.mapex;
//1.com.example.mapex 해당 패키지 이름 복사

//★★★ GPS로 내 위치 찾기 :   AndroidManifest.xml
// 권한  ==> .ACCESS_FINE_LOCATION
// 첫 스타트가 느리다
// 환경적 요소에 민감하다. (집에서는 잘 안될 때가 있다.)
// 모든 위치 기반중에서 가장 정확하다.
// 배터리 소모가 크다.

//★★★wifi 3g 4g 5g 로 내 위치 찾기 :  AndroidManifest.xml
// 권한  ==> .ACCESS_COARSE_LOCATION
// 주변에 와이파이에 영향을 미치지만 빠르다.
// 실내, 실외 모두 사용 가능
// 오차 범위가 크다.
//배터리 소모가 작다.

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
//2.xml 설정 (리니러레이아웃을 전체로 묶어 사용한다.
//3.FragmentActivity를 상속받는 것이 아닌 AppCompatActivity 를 상속받으므로 변경한다.


    private GoogleMap mMap; //구글맵으로 시작하면 자동으로 구글맵 관련 변수 선언과 함께 캐스팅까지 해준다.
    Spinner spSeoulTour;
    Button btnMyPosition;
    String seoul[] = {"국립중앙 박물관", "남산골 한옥마을", "예술의 전당", "청계천", "63 빌딩", "서울 타워(남산타워)", "경복궁", "김치문화 체험관", "서울 올림픽 기념관", "국립 민속 박물관", "서대문 형무소 역사관", "창덕궁"};  // 4.db 또는 자료 입력할 배열선언
    String tourName;    //9. 문자열 변수 선언
    double lat[] = {37.5240867, 37.5591447, 37.4785361, 37.5696512, 37.5198158, 37.5511147, 37.5788408, 37.5629457, 37.5202976, 37.5815645, 37.5742887, 37.5826041};  //10.위도를 가져올 배열 변수 선언
    double lng[] = {126.9803881, 126.9936826, 127.0107423, 127.0056375, 126.9403139, 126.9878596, 126.9770162, 126.9851652, 127.1159236, 126.9789313, 126.9562269, 126.9919376};  //10.경도를 가져올 배열 변수 선언
    LatLng latLng1;
    int pCheck;
    Double tourLatLng[] = new Double[2];//19. 0번째방 = 내가가려는 위도값  1번째방 = 내가가려는 관광지의 경도값  배열생성
    Double myLatLng[] = new Double[2];  //20. 0번째방 = 나의 현재 위도값  1번째방 = 나의 현재 경도값  배열생성
    int pos; //23.  ★★ 정수형 변수 선언  ->


    LocationManager myLocation; //36.LocationManager  변수  선언
    LocationListener listener; //37. LocationListener 변수 선언
    String locationProvider;  //45. LocationProvider 변수 선언
    boolean chk = false;  //38. boolean 변수 선언 ★★ 내 위치냐(버튼을 눌렀을 떄), 관광지 위치냐를 결정해주는 변수
    //39.

    //2가지 방법이 있다.
    // LocationManager 내 폰의 위치정보      FusedLocationProvider  내 기기정보

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        spSeoulTour = (Spinner) findViewById(R.id.spSeoulTour);
        btnMyPosition = (Button) findViewById(R.id.btnMyPosition);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, seoul);  //simple_spinner_dropdown_item 좀 넓은것
        spSeoulTour.setAdapter(adapter);   //6..배열을 사용하기 위한 어뎁터 선언 후 장착
        mapFragment.getMapAsync(this);


        spSeoulTour.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {  //7.스피너에서 아이템을 선택하면 수행하는 메소드
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {  //8.복사해서 가져오기
//                tourName  = spSeoulTour.getSelectedItem().toString();  //11.스피너에서 선택한 문자값을 넣어준다.
//
//        LatLng seoulTour = new LatLng(lat[position], lng[position]);    //12.해당 내가 선택한  스피너의 위도와(배열),경도(배열)을 가져온다.                                      //  8-1 위도와 경도를 설정한 변수 선언
//        mMap.addMarker(new MarkerOptions().position(seoulTour).title("Tour Name"));  //13. .position 값을 seoulTour 변수대입
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoulTour,15));       //14.   seoulTour 변수 대입                //   8-1 아까 설정한 위도와 경도로 설정해준다.
//                //15..newLatLngZoom 지도를 확대 시켜서 보여줄수 있는 기능이다. 변수 뒤에 정수값을 입력하여 조절한다.
                chk = false;
                tourLatLng[0] = lat[position];  //21. 포지션 값마다 있는 위도값을 변수에 대입해준다
                tourLatLng[1] = lng[position];  //22. 포지션 값마다 있는 경도값을 변수에 대입해준다
                pos = position;   //24.  <-전역변수가 값을 받아 다른곳에서도 사용 가능하도록 한다.
                tourMove(tourLatLng);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnMyPosition.setOnClickListener(new View.OnClickListener() {  //17.버튼을 클릭했을 때 내 위치가 표시되도록 하는 메소드 수행
            @Override
            public void onClick(View v) {
                chk = true;
                tourMove(myLatLng);  //49.여기에 나의 위도와 경도가 있다.
            }
        });
        pCheck = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION); //33. 퍼미션 체크

        if (pCheck != PackageManager.PERMISSION_GRANTED) {  //34. 퍼미션 체크가 되지 않았다면, 안의 메소드 수행
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);//35. 퍼미션을 주세요,

        }
        setMyLocation();  //42.  setMyLocation();
    }


    //onResume 메소드 수행
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        myLocation.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1000, (android.location.LocationListener) listener);

    }

    //맵 콜백 함수 호출에 의한 onMapReady 메소드 시작
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        for(int i =0; i<seoul.length; i++) {
            tourLatLng[0] = lat[i];
            tourLatLng[1] = lng[i];
            tourMove(tourLatLng);

        }

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));                         //5. 시드니에 관련된 내용이 지도에 뜨는 샘플링  ->주석처리


    }

    //옵션 메뉴 생성 메소드 시작
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {   //16. 옵션메뉴를 자바로 만들어서 위성지도와 일반지도로 나눠서 표기되도록 함  (7장 메뉴와 대화상자 295p)
        super.onCreateOptionsMenu(menu);

        menu.add(0, 1, 0, "구글지도");
        menu.add(0, 2, 0, "위성지도");

        return true;
    }
        //옵션 메뉴 선택 시 수행항 작업 메소드 시작
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case 1:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);  //일반지도
                break;
            case 2:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);  //위성지도
                break;


        }
        return super.onOptionsItemSelected(item);
    }
        //위도 경도 를 받아 위치로 이동할 지도 표시 부분
    private void tourMove(Double locationLatLog[]) {       //18.  이 부분을 여러가지로 사용하기 때문에 메소드를 생성하여 사용한다.

        String address[] = {"서울특별시 용산구 서빙고로 137 국립중앙박물관", "서울특별시 중구 퇴계로34길 28 남산한옥마을", "서울특별시 서초구 남부순환로 2364 국립국악원", "서울특별시 종로구 창신동", "서울특별시 영등포구 63로 50 한화금융센터_63",
                "서울특별시 용산구 남산공원길 105 N서울타워", "서울특별시 종로구 삼청로 37 국립민속박물관", "서울특별시 중구 명동2가 32-2", "서울특별시 송파구 올림픽로 448 서울올림픽파크텔", "서울특별시 종로구 삼청로 37 국립민속박물관", "서울특별시 서대문구 통일로 251 독립공원", "서울특별시 종로구 율곡로 99",}; //25. 관광지의 주소를 넣을 배열 선언
        final String tel[] = {"02-2077-9000", "02-2264-4412", "02-580-1300", "02-2290-6114", "02-789-5663", "02-3455-9277", "02-3700-3900", "02-318-7051", "02-410-1354", "02-3704-3114", "02-360-8590", "02-762-8261"};  //26.관광지의 전화번호 넣을 배열 선언
        final String homePage [] = {"http://www.museum.go.kr", "http://hanokmaeul.seoul.go.kr", "http://www.sac.or.kr", "http://www.cheonggyecheon.or.kr", "http://www.63.co.kr", "http://www.nseoultower.com",
                "http://www.royalpalace.go.kr", "http://www.visitseoul.net/kr/article/article.do?_method=view&art_id=49160&lang=kr&m=0004003002009&p=03", "http://www.88olympic.or.kr", "http://www.nfm.go.kr", "http://www.sscmc.or.kr/culture2", "http://www.cdg.go.kr"};
                    //27. 관광지의 홈페이지 주소를 넣을 배열 선언


        if(locationLatLog[0]==null) {
            Toast.makeText(getApplicationContext(),"내 위치 정보를 가져오지 못했습니다.",Toast.LENGTH_SHORT).show();
        }else{
            LatLng tourPos = new LatLng(locationLatLog[0],locationLatLog[1]); //28. 위도와 경도 값
            latLng1=tourPos;
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1,12));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng1));
        MarkerOptions markOpt = new MarkerOptions();   //29. 이 부분은 마커를 내가 직접 제작하겠다라는 의미. 새로운 변수 선언함.
        markOpt.position(latLng1);  //30.마커를 어디에 찍을것인가?  tourPos ->내가찍은 위치의 위도와 경도값을 갖고있다.


        if(chk==false) {
            markOpt.title(address[pos]); //31.마커에 보이는 제목  pos 에는 아까 전역변수에서 값을 받은것이 들어와있다.
            markOpt.snippet(tel[pos] + " ( "+ homePage[pos] + " ) ");
        }else {
            markOpt.title("현재 내 위치");
            markOpt.snippet("위도 : "+myLatLng[0]+"경도 : "+myLatLng[1]);
        }

        markOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.map36));  //마커 이미지
        mMap.addMarker(markOpt).showInfoWindow();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {  // 홈페이지 부분을 클릭하면
            @Override
            public boolean onMarkerClick(Marker marker) {
                //해당 광광지의 홈페이지

                if(chk==false) {
                    Uri uri = Uri.parse(homePage[pos]);
                    Intent intent1 = new Intent(Intent.ACTION_VIEW,uri);
                    startActivity(intent1);
                }
                return false;
            }
        });
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() { //전화번호부분 클릭하면
            @Override
            public void onInfoWindowClick(Marker marker) {
                if(chk==false) {
                    Uri uri = Uri.parse("tel:"+tel[pos]);
                    Intent intent2 = new Intent(Intent.ACTION_DIAL,uri);
                    startActivity(intent2);
                }else {
                    Uri uri = Uri.parse("tel:010-5320-2342");
                    Intent intent3 = new Intent(Intent.ACTION_DIAL,uri);
                    startActivity(intent3);
                }


                //해당관광지의 전화연결
            }
        });

    }

        //퍼미션 체크 허락 시 수행할 메소드 시작



    private void setMyLocation() {//41. setMyLocation 메소드 생성



            myLocation = (LocationManager)getSystemService(Context.LOCATION_SERVICE); //43.LOCATION_SERVICE 를 가져옴  (자신의 위치정보를 가져옴)
            locationProvider = myLocation.getBestProvider(new Criteria(),true); //46.  .getBestProvider 가장 빠르게 받아올 수 있는 통신방법으로 내 위치를 찾게 해주는 내용(다만 gps가 우선)
//              myLocation.isProviderEnabled(LocationManager.NETWORK_PROVIDER); //와이파이로만 내 위치를 찾겠다.
//              myLocation.isProviderEnabled(LocationManager.GPS_PROVIDER);  //gps로만 내 위치를 찾겠다.
            listener  = new LocationListener() {  //44.LocationListener 메소드 생성
                @Override
                public void onLocationChanged(Location location) {   //내 앱을 킬때마다 위치가 변경되면 오는 메소드

                    myLatLng[0] = location.getLatitude();  //47. 나의 위도를 받는다. (받기만함)
                    myLatLng[1] = location.getLongitude(); //48.나의 경도를 받는다. (받기만함)
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {  //상태 변경    int status 현재상태를 가져옴
                    switch (status) {
                        case  LocationProvider.OUT_OF_SERVICE:  //통신 서비스 구역을 벗어남
                            Toast.makeText(getApplicationContext(),"통신 서비스 구역을 벗어남.",Toast.LENGTH_SHORT).show();
                            break;
                        case LocationProvider.TEMPORARILY_UNAVAILABLE://일시적 불능 상태
                            Toast.makeText(getApplicationContext(),"일시적 불능 상태.",Toast.LENGTH_SHORT).show();
                            break;
                        case LocationProvider.AVAILABLE: //사용가능
                            Toast.makeText(getApplicationContext(),"지금 사용이 가능합니다.",Toast.LENGTH_SHORT).show();
                            break;
                    }

                }

                @Override
                public void onProviderEnabled(String provider) { //통신가능시 진행되는 메소드
                    Toast.makeText(getApplicationContext(),"지금 연결이 가능합니다.",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onProviderDisabled(String provider) {  //네트워크나 통신 서비스 사용 불가시 진행되는 메소드
                    Toast.makeText(getApplicationContext(),"지금 연결이 불가합니다.",Toast.LENGTH_SHORT).show();

                }
            };


    }
}
