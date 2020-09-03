
curl -w "\n\n%{time_connect} + %{time_starttransfer} = %{time_total}\n" \
  -XPOST 'http://localhost:9200/shapes/country/_search?pretty&request_cache=true' -d '
   {
      "size": 0,
      "_source": ["title", "geoPoint"],
      "query": {
      "bool": {
          "must": {
            "match_all": {}
          },
          "filter" : {
              "geo_polygon" : {
                     "geoPoint" : {
                     "points" : [
                        [ -0.89196408624284, 46.975820414303 ], [ -0.88901027465591, 46.970996440972 ], [ -0.87712517316223, 46.968538385199 ], [ -0.88359502600069, 46.962369264823 ], [ -0.87713915299698, 46.955855606347 ], [ -0.8837974960632, 46.950388590831 ], [ -0.87298436818273, 46.944344247689 ], [ -0.85194566174473, 46.946506302307 ], [ -0.84069799142137, 46.933043168191 ], [ -0.8291494586578, 46.933361861306 ], [ -0.8226181521192, 46.919496882097 ], [ -0.80739348796693, 46.9198254879 ], [ -0.81977358192379, 46.909085252586 ], [ -0.82020445366662, 46.899619563541 ], [ -0.8321856982677, 46.884537016923 ], [ -0.81527450811283, 46.879362263307 ], [ -0.80837576068615, 46.869152331931 ], [ -0.79477643718776, 46.861061682533 ], [ -0.78164600263284, 46.84282764491 ], [ -0.75815720893345, 46.831399908556 ], [ -0.74431663878843, 46.830243411726 ], [ -0.72752746779957, 46.821938243707 ], [ -0.70883784005248, 46.821063616454 ], [ -0.7008577279242, 46.80862581328 ], [ -0.71783828712373, 46.800521372146 ], [ -0.72731204616972, 46.76763065568 ], [ -0.72079949738479, 46.762538021138 ], [ -0.71534345605879, 46.751789737735 ], [ -0.70417950073791, 46.74945353126 ], [ -0.69413275947291, 46.742731111953 ], [ -0.70015414355827, 46.73579562558 ], [ -0.68397597689122, 46.727863770277 ], [ -0.66888364550954, 46.717232370405 ], [ -0.65619325740221, 46.700774517445 ], [ -0.68073310425929, 46.686803996225 ], [ -0.65787343587941, 46.676827080382 ], [ -0.63771460553181, 46.66348907218 ], [ -0.64924847916988, 46.653283801506 ], [ -0.65717243733762, 46.634619354943 ], [ -0.64405974899964, 46.638024434086 ], [ -0.62619906297455, 46.633078356802 ], [ -0.61411399285986, 46.62039129053 ], [ -0.62702650527698, 46.605651097302 ], [ -0.61671038535954, 46.598536566433 ], [ -0.61190793736705, 46.588307031212 ], [ -0.62456684878994, 46.577400290716 ], [ -0.61785426354446, 46.562043099519 ], [ -0.60670934401643, 46.562331917411 ], [ -0.60213244964129, 46.533279525109 ], [ -0.61002538912342, 46.52729145947 ], [ -0.63352903140625, 46.526545961083 ], [ -0.64519375671477, 46.5085710348 ], [ -0.636581601251, 46.506095217567 ], [ -0.62483750061224, 46.496362745962 ], [ -0.6241822995269, 46.48674665851 ], [ -0.6124273279524, 46.45873668738 ], [ -0.61983716832458, 46.452467117452 ], [ -0.61877090027571, 46.438854562232 ], [ -0.63684012869206, 46.432305656566 ], [ -0.6406468690526, 46.416224664644 ], [ -0.63282952674398, 46.403812558564 ], [ -0.63658749242212, 46.395578228534 ], [ -0.62088828506372, 46.390451131851 ], [ -0.61017966919341, 46.413733221347 ], [ -0.59417281123848, 46.410185950063 ], [ -0.58189775121858, 46.40217377528 ], [ -0.57238400631421, 46.400707224569 ], [ -0.56605536149932, 46.393082159377 ], [ -0.55052040300574, 46.393343323056 ], [ -0.53779518169029, 46.38646382767 ], [ -0.55764744918976, 46.363451353574 ], [ -0.57567653828611, 46.356508034766 ], [ -0.60325195656142, 46.361468010185 ], [ -0.60547077785894, 46.347207865876 ], [ -0.61868206115959, 46.339099077209 ], [ -0.63660376728023, 46.33759543996 ], [ -0.63998183318338, 46.322110972748 ], [ -0.64825320981615, 46.317143530802 ], [ -0.67227364916502, 46.316216425466 ], [ -0.69732735736344, 46.325092837487 ], [ -0.70757171772654, 46.317704940913 ], [ -0.7201487066083, 46.314890543328 ], [ -0.72191414100459, 46.302372661742 ], [ -0.73459543046899, 46.304955701863 ], [ -0.75047134037184, 46.304254425344 ], [ -0.75794915819339, 46.311254056955 ], [ -0.77570312778264, 46.318376375981 ], [ -0.78424343720168, 46.318861879535 ], [ -0.80247564445275, 46.325156566217 ], [ -0.80732128185426, 46.339293680039 ], [ -0.82415169186325, 46.335854967946 ], [ -0.83052754444062, 46.341529547924 ], [ -0.83988457052554, 46.340367099668 ], [ -0.84865539686859, 46.332512597898 ], [ -0.84487598314026, 46.3245597282 ], [ -0.8509424448661, 46.317185675508 ], [ -0.88767597392192, 46.326322589931 ], [ -0.89270136944125, 46.320101412175 ], [ -0.90480272235534, 46.313812430336 ], [ -0.93489398189823, 46.312857623927 ], [ -0.9612310671154, 46.323395683014 ], [ -0.9441551264402, 46.336048129241 ], [ -0.93790474501528, 46.353157499224 ], [ -0.92857576719505, 46.371051399922 ], [ -0.94112049397563, 46.367862165606 ], [ -0.95084003340185, 46.360617622608 ], [ -0.96451829359621, 46.365398033698 ], [ -0.97744453847283, 46.351108789901 ], [ -0.99521192821645, 46.350317645688 ], [ -1.013809705621, 46.35562286986 ], [ -1.0290641764217, 46.348867660129 ], [ -1.0525331368763, 46.342540560637 ], [ -1.0645117099073, 46.335517206168 ], [ -1.0807238252872, 46.321431313321 ], [ -1.0782951920661, 46.316911662518 ], [ -1.0997364979128, 46.314471710528 ], [ -1.113284059211, 46.316302232447 ], [ -1.1236420717667, 46.321795835911 ], [ -1.1294064331873, 46.310272328818 ], [ -1.1396042849062, 46.311220270269 ], [ -1.1633871701049, 46.324357697468 ], [ -1.1789816995107, 46.319237198363 ], [ -1.2010861494985, 46.316343408957 ], [ -1.2029476108593, 46.31299774522 ], [ -1.1955066954319, 46.300486336512 ], [ -1.2063790183713, 46.288844677353 ], [ -1.2073980363312, 46.266566974958 ], [ -1.233813730916, 46.279787036182 ], [ -1.244708256192, 46.287909226104 ], [ -1.2850844462619, 46.310943853415 ], [ -1.2958949852744, 46.323927342846 ], [ -1.3059782500842, 46.319190709176 ], [ -1.2950494049459, 46.30445585614 ], [ -1.298649918542, 46.298035215363 ], [ -1.3088839142276, 46.303381059905 ], [ -1.3209210907591, 46.321997394722 ], [ -1.3209769583137, 46.325733328602 ], [ -1.3456750283516, 46.34200224737 ], [ -1.3667385250608, 46.348611637782 ], [ -1.3817097975849, 46.341186215234 ], [ -1.4009756575246, 46.340508325831 ], [ -1.416442893161, 46.346032461982 ], [ -1.4284201012028, 46.347121382446 ], [ -1.4369731247534, 46.341049782884 ], [ -1.4659374486589, 46.342356465827 ], [ -1.4709432372166, 46.346061757066 ], [ -1.4771749719261, 46.363507201844 ], [ -1.4878020252489, 46.38139003894 ], [ -1.5021398541908, 46.397345234333 ], [ -1.5150666516907, 46.40429394231 ], [ -1.5377511402152, 46.409241650902 ], [ -1.5505012086214, 46.405427866679 ], [ -1.5849335376726, 46.408882191167 ], [ -1.6113093601133, 46.413418180507 ], [ -1.6270620551428, 46.414220589438 ], [ -1.6417221223658, 46.419202887095 ], [ -1.6563835193513, 46.431211054285 ], [ -1.6746027257341, 46.439923752177 ], [ -1.7038680334017, 46.448675063621 ], [ -1.7191201629207, 46.459167761425 ], [ -1.7521241921725, 46.474155783424 ], [ -1.777137894573, 46.492758626968 ], [ -1.7912930929301, 46.494497528609 ], [ -1.8033948364521, 46.488967027341 ], [ -1.8123442739131, 46.493416566185 ], [ -1.8186497595357, 46.518682691519 ], [ -1.8419594438554, 46.573013536296 ], [ -1.8484416100107, 46.585884833838 ], [ -1.8560152427178, 46.608580735068 ], [ -1.873382171513, 46.620915463581 ], [ -1.8822873872759, 46.630594948835 ], [ -1.8906734495839, 46.63469093819 ], [ -1.9035668912462, 46.647321849326 ], [ -1.9126335673886, 46.661558294252 ], [ -1.9428758596327, 46.692706115369 ], [ -1.9508357687079, 46.695651008644 ], [ -1.966128703921, 46.692012127622 ], [ -1.9784453425744, 46.703097268015 ], [ -1.9826649825899, 46.720311181056 ], [ -2.0002660579608, 46.733925400032 ], [ -2.0719487138764, 46.782812921452 ], [ -2.1171981955453, 46.803360927686 ], [ -2.1401926882177, 46.817151505205 ], [ -2.1445060316881, 46.826399169492 ], [ -2.1454334062879, 46.847809766579 ], [ -2.1489184130775, 46.869931347964 ], [ -2.1553427446113, 46.883151727981 ], [ -2.153823981621, 46.890149400164 ], [ -2.1188882449818, 46.893677330801 ], [ -2.1177998913797, 46.908317123363 ], [ -2.1034385508171, 46.920984664355 ], [ -2.062735433411, 46.948064118056 ], [ -2.0447808242546, 46.969115340219 ], [ -2.0453622399129, 46.979481273443 ], [ -2.0273622488649, 47.00992054926 ], [ -1.9975145183017, 47.018754751413 ], [ -1.9804130066429, 47.028904753758 ], [ -1.9836243655281, 47.029504869904 ], [ -1.9994147476381, 47.055759178647 ], [ -2.0047214099724, 47.061460656046 ], [ -2.0326019810958, 47.073513335424 ], [ -2.0485561088483, 47.086376045155 ], [ -2.0534773710676, 47.094116874449 ], [ -2.1048560377353, 47.108515739095 ], [ -2.1555855165283, 47.112834108468 ], [ -2.1776795960367, 47.12206186156 ], [ -2.2150136613872, 47.124060089367 ], [ -2.2268201491195, 47.130935989866 ], [ -2.2478150673853, 47.134022011309 ], [ -2.2295324163069, 47.144165117321 ], [ -2.226128166772, 47.152275320793 ], [ -2.2009562728096, 47.158406462933 ], [ -2.1802049236481, 47.15593411267 ], [ -2.1670631605228, 47.166180557901 ], [ -2.1582127475069, 47.196892590523 ], [ -2.158373708365, 47.209050666096 ], [ -2.1738613907116, 47.22651156379 ], [ -2.1778607172174, 47.236097279547 ], [ -2.1705455113596, 47.239751522169 ], [ -2.1699895453995, 47.268472297219 ], [ -2.1873608303178, 47.280622361747 ], [ -2.2055602597095, 47.271131396915 ], [ -2.2246437954795, 47.264387061764 ], [ -2.2278604253303, 47.256330261162 ], [ -2.2466914047133, 47.255988431427 ], [ -2.2698952747426, 47.2395647121 ], [ -2.3016402822796, 47.236400031931 ], [ -2.3396017707663, 47.255188453422 ], [ -2.3420962103188, 47.261755983579 ], [ -2.3552895948005, 47.27180855813 ], [ -2.369753264591, 47.277438171535 ], [ -2.3983798514521, 47.281448757206 ], [ -2.4203808400315, 47.276443831705 ], [ -2.4253877232221, 47.270964820355 ], [ -2.4165529129684, 47.25871256282 ], [ -2.4475351836802, 47.261757701956 ], [ -2.4555210807153, 47.268139889592 ], [ -2.4823147658717, 47.272979613928 ], [ -2.4990968218639, 47.280741885999 ], [ -2.5442517230455, 47.290107777197 ], [ -2.5449696912262, 47.297963681667 ], [ -2.5272130839477, 47.301551808752 ], [ -2.5138083846226, 47.298375734085 ], [ -2.5049900911185, 47.31367062334 ], [ -2.5029144731486, 47.328754801261 ], [ -2.5067783337792, 47.341390242029 ], [ -2.5211591992751, 47.358811002331 ], [ -2.531174634228, 47.365374694297 ], [ -2.5413465599302, 47.366005665021 ], [ -2.5589448655806, 47.374566485616 ], [ -2.5455487941567, 47.381126464909 ], [ -2.5341061627856, 47.382961282244 ], [ -2.5224115438117, 47.392240089144 ], [ -2.5000251663727, 47.404398607793 ], [ -2.4912351567464, 47.404811002726 ], [ -2.4826825430466, 47.412264457829 ], [ -2.4726723242774, 47.416083446873 ], [ -2.4583086370333, 47.412128481989 ], [ -2.4347120386263, 47.41323956486 ], [ -2.4330361363116, 47.416778409153 ], [ -2.450864597211, 47.425323844427 ], [ -2.4523914498156, 47.43382171003 ], [ -2.4482478800515, 47.441292494065 ], [ -2.4584933200854, 47.44812333026 ], [ -2.453436631799, 47.46207522621 ], [ -2.4400824549861, 47.465780511452 ], [ -2.4230228667484, 47.477116356867 ], [ -2.4168260809589, 47.462051729438 ], [ -2.399942719827, 47.455985214433 ], [ -2.390389834657, 47.456871614258 ], [ -2.3822957470498, 47.462247958261 ], [ -2.3709692377347, 47.463384419512 ], [ -2.3540439813745, 47.454520038759 ], [ -2.3463648513449, 47.457849990711 ], [ -2.3239404651198, 47.459700150766 ], [ -2.3128644981072, 47.464470727328 ], [ -2.3131411665125, 47.485904562585 ], [ -2.3036714616823, 47.49241124835 ], [ -2.2991228743726, 47.500477533121 ], [ -2.2965137349146, 47.51605888675 ], [ -2.2808336661554, 47.509587432992 ], [ -2.2629685042061, 47.512911091518 ], [ -2.2585200153358, 47.504528170665 ], [ -2.2656383885011, 47.501780337272 ], [ -2.2442830226806, 47.493603343394 ], [ -2.2199696788078, 47.505419526219 ], [ -2.2066684734953, 47.510040688056 ], [ -2.184622799181, 47.511938749674 ], [ -2.1837606578958, 47.501791543642 ], [ -2.192163736718, 47.496643815865 ], [ -2.1833018888491, 47.491696689458 ], [ -2.1636756892187, 47.49050097161 ], [ -2.1541860993449, 47.496364966339 ], [ -2.1529569093967, 47.510962718509 ], [ -2.1560180197876, 47.522028206659 ], [ -2.1073294072614, 47.531054380017 ], [ -2.0985591504874, 47.533957226787 ], [ -2.096829841083, 47.540329954312 ], [ -2.1038869792414, 47.549970178949 ], [ -2.096507139486, 47.572369157778 ], [ -2.1037228936324, 47.589435010495 ], [ -2.0992687458213, 47.597488169512 ], [ -2.0869236114994, 47.602779172942 ], [ -2.0849952925543, 47.621229400095 ], [ -2.0970339249479, 47.631356309182 ], [ -2.0898837230131, 47.642709543195 ], [ -2.0747327428802, 47.651662965349 ], [ -2.0579097595869, 47.649483633439 ], [ -2.050624831954, 47.651134018014 ], [ -2.0430946586878, 47.665696588636 ], [ -2.0357277498302, 47.668540889438 ], [ -2.013316284318, 47.666003385464 ], [ -2.0093555248025, 47.671376463587 ], [ -1.9852234827543, 47.683232805983 ], [ -1.9743811536912, 47.693936715593 ], [ -1.9690592974, 47.688368806165 ], [ -1.9691960862159, 47.677526213073 ], [ -1.9538162196423, 47.672268062701 ], [ -1.9363036351526, 47.686649217665 ], [ -1.8918360415668, 47.696328031406 ], [ -1.880073341802, 47.695308629028 ], [ -1.8640156728505, 47.706980966313 ], [ -1.8415465142207, 47.705493872656 ], [ -1.8250533703276, 47.708269117018 ], [ -1.8028599988222, 47.702303252055 ], [ -1.7724177137179, 47.698454240157 ], [ -1.754669062014, 47.70617243813 ], [ -1.7354918450583, 47.704030670166 ], [ -1.7292404629403, 47.699070332406 ], [ -1.7133831145586, 47.699308113335 ], [ -1.7051414585604, 47.709321517725 ], [ -1.6863422821157, 47.713034371357 ], [ -1.6641158381935, 47.711144721547 ], [ -1.6548173400684, 47.712589314425 ], [ -1.6454761757831, 47.721464189516 ], [ -1.6381811285931, 47.72231109575 ], [ -1.6390681088186, 47.731229794449 ], [ -1.6355680463584, 47.74265760999 ], [ -1.6260806089052, 47.756571625148 ], [ -1.6163542814852, 47.764155016463 ], [ -1.5981151893439, 47.766615164584 ], [ -1.5934046637562, 47.776049297939 ], [ -1.5519088415196, 47.784014922086 ], [ -1.5280610367888, 47.785843354761 ], [ -1.5202764448571, 47.793620421696 ], [ -1.5042386939766, 47.800947509052 ], [ -1.4928971911208, 47.798439989341 ], [ -1.4684440000216, 47.8059033883 ], [ -1.4669179596494, 47.809780189045 ], [ -1.4818132504128, 47.831893536154 ], [ -1.4628972269562, 47.833557723029 ], [ -1.435426799044, 47.83115216305 ], [ -1.4249168477477, 47.832841359769 ], [ -1.4178500987788, 47.827486856246 ], [ -1.3904289319245, 47.828276258082 ], [ -1.3813446142742, 47.822668998285 ], [ -1.3772932944092, 47.812713141041 ], [ -1.3631664145966, 47.801683607753 ], [ -1.3528652871869, 47.797688995885 ], [ -1.3184870728098, 47.792334376846 ], [ -1.2458850128779, 47.776717450948 ], [ -1.238247803597, 47.809992506553 ], [ -1.2327236903989, 47.820244561296 ], [ -1.2206362424472, 47.820389620636 ], [ -1.2139544800221, 47.844315066059 ], [ -1.222737283795, 47.852599843541 ], [ -1.2164918382318, 47.857201283697 ], [ -1.2030225271774, 47.856844148885 ], [ -1.1891865993477, 47.867976952617 ], [ -1.1969660077971, 47.8789391525 ], [ -1.1966306275523, 47.889267731968 ], [ -1.1762242926663, 47.897399695909 ], [ -1.1754814134505, 47.910385737305 ], [ -1.1663141666181, 47.923578184002 ], [ -1.1671197731246, 47.93471641408 ], [ -1.1595152529454, 47.939219922042 ], [ -1.1613811233462, 47.952310307885 ], [ -1.1539896711831, 47.96581664419 ], [ -1.1343829543823, 47.969309177864 ], [ -1.1260752876073, 47.973307719181 ], [ -1.1228021186321, 47.986671335599 ], [ -1.1026780017247, 47.989064346633 ], [ -1.0908100123801, 47.98774338815 ], [ -1.0709659537463, 47.981801155317 ], [ -1.045096271371, 47.987097320485 ], [ -1.021259621245, 47.994939309553 ], [ -1.0168893967587, 48.003728266054 ], [ -1.0182078693094, 48.012308274749 ], [ -1.0330632921231, 48.031188955766 ], [ -1.0277969380856, 48.044863699713 ], [ -1.033830641773, 48.05209341916 ], [ -1.0232901182126, 48.068911872714 ], [ -1.0406036968359, 48.078179883231 ], [ -1.0496111794277, 48.089801151393 ], [ -1.0527563302629, 48.10735152844 ], [ -1.0591416106668, 48.125077547289 ], [ -1.0602880762288, 48.15011075649 ], [ -1.0738876778536, 48.159661823604 ], [ -1.079605768256, 48.183480590178 ], [ -1.0747741638529, 48.198201373069 ], [ -1.0873107692067, 48.209806916083 ], [ -1.0806036422976, 48.21949752801 ], [ -1.0865989301563, 48.227529635817 ], [ -1.1000554557338, 48.25927761239 ], [ -1.0930408735111, 48.281855553692 ], [ -1.0820920155689, 48.298391604095 ], [ -1.0592143517314, 48.312093827381 ], [ -1.0450195990474, 48.32772872854 ], [ -1.0558182376068, 48.340683235403 ], [ -1.0598919207817, 48.350532594956 ], [ -1.0590029395387, 48.358994435228 ], [ -1.0646292178107, 48.368281337083 ], [ -1.053937234376, 48.383986913354 ], [ -1.0681208970941, 48.404716742704 ], [ -1.0783677064971, 48.413230711968 ], [ -1.0779200339792, 48.421477186884 ], [ -1.0827926455739, 48.433057379747 ], [ -1.0793641401546, 48.443271982049 ], [ -1.0654330620157, 48.451695678779 ], [ -1.0639645498148, 48.466954435587 ], [ -1.0741061961301, 48.473896847289 ], [ -1.0782957929126, 48.481154857966 ], [ -1.075717285974, 48.499392729686 ], [ -1.0701643748629, 48.508492017418 ], [ -1.0605497194866, 48.515346429697 ], [ -1.0514451903751, 48.509308794368 ], [ -1.0039961840993, 48.489172448089 ], [ -0.97225823660351, 48.494600246107 ], [ -0.96235389107925, 48.503667410902 ], [ -0.96425363340344, 48.510812466339 ], [ -0.95637394558324, 48.516620056723 ], [ -0.93371106054579, 48.51502659051 ], [ -0.92236046915212, 48.512389219916 ], [ -0.91847065624527, 48.500394275132 ], [ -0.89624907070572, 48.495083815873 ], [ -0.8774635305069, 48.499620464013 ], [ -0.86036021134895, 48.501458584456 ], [ -0.84610706675308, 48.498284307396 ], [ -0.83778925434372, 48.485178672867 ], [ -0.82728981063172, 48.476292220178 ], [ -0.8184585148882, 48.474291742186 ], [ -0.81322359143068, 48.455083144137 ], [ -0.79918376742683, 48.458939062295 ], [ -0.79756269441243, 48.465280274577 ], [ -0.77787285065978, 48.465413522216 ], [ -0.7785859196383, 48.453255439492 ], [ -0.77453904965747, 48.44327891903 ], [ -0.75727715268855, 48.436552496914 ], [ -0.73527798039621, 48.445048872201 ], [ -0.71509947351088, 48.448950147648 ], [ -0.7197695922356, 48.454578580623 ], [ -0.73585755427354, 48.461124917636 ], [ -0.73034187148783, 48.472703026439 ], [ -0.71121688547931, 48.470742279355 ], [ -0.70206934477695, 48.467207668633 ], [ -0.68799234091834, 48.469431032111 ], [ -0.68585273590642, 48.475468439014 ], [ -0.66895705284659, 48.486137900646 ], [ -0.66371844747552, 48.484471551488 ], [ -0.65363074848858, 48.459545681615 ], [ -0.6540003356242, 48.444278312957 ], [ -0.6175695455761, 48.458960402325 ], [ -0.59533674901115, 48.472630277201 ], [ -0.57152013702789, 48.469152972446 ], [ -0.55171796610154, 48.473119783464 ], [ -0.54510290980273, 48.482691035747 ], [ -0.5304424347088, 48.495164970256 ], [ -0.50506155077299, 48.505798828433 ], [ -0.4884950789804, 48.501617721865 ], [ -0.47820460128897, 48.501565713987 ], [ -0.47060334996255, 48.509716651644 ], [ -0.46226332328429, 48.512709151203 ], [ -0.43075652123879, 48.51181625112 ], [ -0.42497181740742, 48.507282953736 ], [ -0.41273449116925, 48.506498004612 ], [ -0.3991855056689, 48.510158714286 ], [ -0.39345664138171, 48.501835194106 ], [ -0.3676233888649, 48.492944315138 ], [ -0.36723974711241, 48.487748985001 ], [ -0.35349558279054, 48.483897081468 ], [ -0.35582138041634, 48.495673571366 ], [ -0.34337759223335, 48.500849863584 ], [ -0.32023332020671, 48.522923755915 ], [ -0.30280889062491, 48.517340998248 ], [ -0.27823048075492, 48.506986142113 ], [ -0.27155353070113, 48.507447568195 ], [ -0.26580141522268, 48.522782191648 ], [ -0.25395512234333, 48.525985631213 ], [ -0.24176789393774, 48.536388956089 ], [ -0.24635660161985, 48.542620878313 ], [ -0.26180425334438, 48.54789519181 ], [ -0.24264015511442, 48.567994064435 ], [ -0.23435242732491, 48.562336320193 ], [ -0.22107424204732, 48.560317301834 ], [ -0.20694246592994, 48.562946447212 ], [ -0.19398633186269, 48.554824404734 ], [ -0.1899589987284, 48.548884398226 ], [ -0.16937878261294, 48.536973156988 ], [ -0.14460271813847, 48.527754338423 ], [ -0.14501210976342, 48.521000343601 ], [ -0.15568415360731, 48.520496772763 ], [ -0.16634047065468, 48.51558387484 ], [ -0.1720909995602, 48.502134649047 ], [ -0.15856042497356, 48.496817021301 ], [ -0.14958860992305, 48.479781866844 ], [ -0.15336586853021, 48.476724917968 ], [ -0.14871763477939, 48.458069224847 ], [ -0.12454179598033, 48.449239552758 ], [ -0.10641182796282, 48.447519773766 ], [ -0.073006901765828, 48.450527118516 ], [ -0.072707535476378, 48.456927943118 ], [ -0.051890589271637, 48.453255414135 ], [ -0.049909790963035, 48.447628170085 ], [ -0.057355677768335, 48.42850299559 ], [ -0.053012801512314, 48.412716132961 ], [ -0.05669039654505, 48.398915618731 ], [ -0.052691042298523, 48.392979240085 ], [ -0.054527208218665, 48.382004461206 ], [ -0.050692623162423, 48.375201195715 ], [ -0.035753099663116, 48.384874683334 ], [ -0.022054721991416, 48.388059994444 ], [ -0.020363541559243, 48.393656323667 ], [ -0.0025641291874286, 48.397311951396 ], [ 0.0065863250934992, 48.388521385965 ], [ 0.020992722816365, 48.380200925309 ], [ 0.062489872264419, 48.382213868151 ], [ 0.056684859958794, 48.393974400454 ], [ 0.067826622948661, 48.406115400716 ], [ 0.083580132468228, 48.411137710408 ], [ 0.09917046478078, 48.41034986941 ], [ 0.11624768586472, 48.435555660302 ], [ 0.15131906064063, 48.437226845695 ], [ 0.15811789380158, 48.4440164218 ], [ 0.15610127663516, 48.454794932825 ], [ 0.16965670000587, 48.449364042801 ], [ 0.16972375424173, 48.461776714307 ], [ 0.18125494041244, 48.464965078387 ], [ 0.18981304225187, 48.461891344828 ], [ 0.21823241324651, 48.473790546424 ], [ 0.22939338887665, 48.472578001816 ], [ 0.25857807031538, 48.476710383834 ], [ 0.26286221529839, 48.482954540393 ], [ 0.27593434536664, 48.479055127239 ], [ 0.29585588821524, 48.480174860693 ], [ 0.31789727296834, 48.471938210212 ], [ 0.32727632603609, 48.471072305832 ], [ 0.33874148490671, 48.461599536909 ], [ 0.35578465090337, 48.458217063582 ], [ 0.36395632992898, 48.451631721658 ], [ 0.36771793204951, 48.438272682172 ], [ 0.38066015166482, 48.425411796164 ], [ 0.38150787714381, 48.417547978808 ], [ 0.37172370405574, 48.410451667862 ], [ 0.37537215739256, 48.395740224588 ], [ 0.37386118731698, 48.386969757082 ], [ 0.37865893857906, 48.383227765197 ], [ 0.38255236381737, 48.359498801305 ], [ 0.38828549911199, 48.349122009475 ], [ 0.38047838823167, 48.341797530871 ], [ 0.38260989677575, 48.333828412247 ], [ 0.3954029644226, 48.320549965535 ], [ 0.4062203560807, 48.314621149851 ], [ 0.41599578567133, 48.321625198799 ], [ 0.42688901989368, 48.315425242308 ], [ 0.43133408375288, 48.306638667446 ], [ 0.44279870606142, 48.304629310064 ], [ 0.46361198113469, 48.305016107657 ], [ 0.48050090924161, 48.298592258919 ], [ 0.48757654033176, 48.307795859172 ], [ 0.50702990447062, 48.295832610533 ], [ 0.49455647283795, 48.28681567575 ], [ 0.51293004449183, 48.266874483366 ], [ 0.5303000454288, 48.265496730429 ], [ 0.53848634108307, 48.256987820339 ], [ 0.53597049486367, 48.249844560134 ], [ 0.55013843367508, 48.249395520335 ], [ 0.56099429972633, 48.245949063769 ], [ 0.57919146406902, 48.24436440048 ], [ 0.63315984470535, 48.245553870078 ], [ 0.63190429889268, 48.254754506701 ], [ 0.64070530481206, 48.261221689671 ], [ 0.65315095543918, 48.263702677988 ], [ 0.67547108490982, 48.254740726504 ], [ 0.68321966914477, 48.2485882228 ], [ 0.7165758658899, 48.212094515686 ], [ 0.72363045806996, 48.19813955141 ], [ 0.73014979083029, 48.200521766169 ], [ 0.73782963836386, 48.189069627691 ], [ 0.75566254080812, 48.181981836049 ], [ 0.76407918474979, 48.181599665308 ], [ 0.79765841643139, 48.19445496608 ], [ 0.79562670501545, 48.188043105362 ], [ 0.80835901939965, 48.18611890903 ], [ 0.82688955262783, 48.175387000045 ], [ 0.83683452952269, 48.167352245345 ], [ 0.86198925121346, 48.166816904566 ], [ 0.88249645680993, 48.161766033434 ], [ 0.91161206421963, 48.148858532659 ], [ 0.91379809767445, 48.135125048448 ], [ 0.89396954177329, 48.135535055964 ], [ 0.87293551592102, 48.133408559058 ], [ 0.852575219489, 48.133602174445 ], [ 0.8553701454218, 48.122620579792 ], [ 0.84121734187884, 48.103059710788 ], [ 0.83268783335498, 48.098453526315 ], [ 0.81435131408069, 48.098801731111 ], [ 0.81516825904503, 48.093730753222 ], [ 0.83920278539184, 48.09125189303 ], [ 0.84483764311698, 48.086647072566 ], [ 0.84302009925743, 48.072638200667 ], [ 0.83460349982458, 48.070147734707 ], [ 0.80118315423495, 48.071513555954 ], [ 0.79653309159021, 48.05267766261 ], [ 0.79747785616118, 48.037556987677 ], [ 0.80877017226921, 48.031993621434 ], [ 0.82520263947346, 48.03005952554 ], [ 0.83672342747852, 48.034558745892 ], [ 0.84155459904793, 48.029673676539 ], [ 0.84052666414894, 48.021048593652 ], [ 0.83170184588828, 48.006116934727 ], [ 0.83237463233502, 47.996592094392 ], [ 0.82622288599262, 47.991475839362 ], [ 0.82428057343045, 47.982142037161 ], [ 0.84529238839341, 47.954438931698 ], [ 0.84579379193831, 47.941403140329 ], [ 0.83727689039754, 47.937246716219 ], [ 0.8171459594605, 47.934467914387 ], [ 0.81211830142135, 47.928939306036 ], [ 0.80913428200565, 47.91066247592 ], [ 0.81721753097407, 47.892418803105 ], [ 0.81018749426364, 47.890393591962 ], [ 0.79799062776542, 47.898193546758 ], [ 0.79002362904946, 47.912210300533 ], [ 0.78051199587287, 47.910375216053 ], [ 0.77032460906914, 47.902009083915 ], [ 0.75986941585155, 47.898224938894 ], [ 0.75733759497489, 47.884473514914 ], [ 0.76468938104337, 47.866582667793 ], [ 0.75916076452252, 47.859222188506 ], [ 0.77401870735853, 47.851208382865 ], [ 0.77457358541314, 47.839684494777 ], [ 0.76841445732376, 47.831101351932 ], [ 0.7588569724751, 47.833536394698 ], [ 0.74540046495951, 47.825663057103 ], [ 0.7397212944059, 47.814678933449 ], [ 0.72484407861962, 47.798889067315 ], [ 0.71263236035006, 47.790038970809 ], [ 0.69798475487217, 47.788889445319 ], [ 0.68931761050545, 47.779996503151 ], [ 0.70344170542358, 47.769940283327 ], [ 0.69688004266227, 47.764225211 ], [ 0.67564997055176, 47.768962404729 ], [ 0.63937932915856, 47.751572315523 ], [ 0.62683335231018, 47.751793159891 ], [ 0.61066958830157, 47.732034198596 ], [ 0.61159726872368, 47.728134311986 ], [ 0.59409530653203, 47.723105855723 ], [ 0.58052041667909, 47.712330763793 ], [ 0.59297009308968, 47.703590911989 ], [ 0.59557114412559, 47.688312714504 ], [ 0.60418662363095, 47.685607124967 ], [ 0.61443245110168, 47.694215472574 ], [ 0.61480416648326, 47.68275087954 ], [ 0.60463738045699, 47.679968482062 ], [ 0.58772733155021, 47.669617061038 ], [ 0.55947922009731, 47.665994923777 ], [ 0.54289746214182, 47.656203651505 ], [ 0.51325174134335, 47.652863992861 ], [ 0.4996666538331, 47.645272415067 ], [ 0.4797670863672, 47.64329220016 ], [ 0.47607646347987, 47.648011563852 ], [ 0.45662804145159, 47.638826353606 ], [ 0.45518198840301, 47.627017225989 ], [ 0.44993291932188, 47.619329777154 ], [ 0.42390524684004, 47.617824451176 ], [ 0.39702255940301, 47.638927008339 ], [ 0.38107503178189, 47.639064909321 ], [ 0.36465430735157, 47.626011449659 ], [ 0.36480446047317, 47.620165400372 ], [ 0.37905609046241, 47.610779501442 ], [ 0.39442325665444, 47.594393009892 ], [ 0.40216586206278, 47.579002216555 ], [ 0.3789542711163, 47.569104805534 ], [ 0.36673611607068, 47.573457923574 ], [ 0.33958580258874, 47.579472315073 ], [ 0.33844630799321, 47.585030259946 ], [ 0.32325488038268, 47.592888415747 ], [ 0.29001704748145, 47.597728628188 ], [ 0.27799234011686, 47.597381208395 ], [ 0.26784211805055, 47.608672360211 ], [ 0.25925309972834, 47.612253732911 ], [ 0.23768269638563, 47.610966183961 ], [ 0.23000044283917, 47.608397360802 ], [ 0.23453049018557, 47.57797744555 ], [ 0.21510267043528, 47.569975576615 ], [ 0.20150198535452, 47.544324126228 ], [ 0.19334553772128, 47.539118278038 ], [ 0.2034912870347, 47.533330991541 ], [ 0.20807030184987, 47.526432351568 ], [ 0.22491484141757, 47.52709951065 ], [ 0.22008941253351, 47.511490494119 ], [ 0.22010664406864, 47.50195233416 ], [ 0.2007181726014, 47.484545277708 ], [ 0.18979781155354, 47.460723327297 ], [ 0.18093772230519, 47.453404824236 ], [ 0.18527899163678, 47.424736116162 ], [ 0.18138204745888, 47.417824739063 ], [ 0.15385793095636, 47.398727021572 ], [ 0.16948959021545, 47.395646092388 ], [ 0.16798792612309, 47.386934066036 ], [ 0.18296214025622, 47.38033044936 ], [ 0.15845843664067, 47.366157059648 ], [ 0.14165395029201, 47.361961427199 ], [ 0.14796868770319, 47.348447803202 ], [ 0.13898837615883, 47.33824318552 ], [ 0.13125388952305, 47.33409233868 ], [ 0.11745694898776, 47.332342613969 ], [ 0.11786940351405, 47.325601141531 ], [ 0.10924461524906, 47.313421774727 ], [ 0.099012801986365, 47.308139754432 ], [ 0.078978960154079, 47.282822156056 ], [ 0.082848635358157, 47.274168568836 ], [ 0.074829460019472, 47.248048474897 ], [ 0.072492820219795, 47.220509854465 ], [ 0.053277684947378, 47.197182170708 ], [ 0.066596690426524, 47.189796025762 ], [ 0.063052886223431, 47.175281799504 ], [ 0.053830055961677, 47.16373374848 ], [ 0.049480342584696, 47.168623012343 ], [ 0.036501918227681, 47.160445278183 ], [ 0.019016376976915, 47.175754285742 ], [ -0.010739414334867, 47.15751215065 ], [ -0.034011786935389, 47.127334734816 ], [ -0.040856468682482, 47.112928627112 ], [ -0.039289561029516, 47.108055925394 ], [ -0.026535185584925, 47.105798471803 ], [ -0.029234974929682, 47.095257561848 ], [ -0.03562437381954, 47.086261232309 ], [ -0.044169213959146, 47.093239781385 ], [ -0.060661742719366, 47.09514763055 ], [ -0.085909274640678, 47.101010256779 ], [ -0.098719424061378, 47.090117668474 ], [ -0.10150441660145, 47.08326376928 ], [ -0.1021158452812, 47.06480003115 ], [ -0.12837866130337, 47.054429041651 ], [ -0.13712137046296, 47.058426719475 ], [ -0.13678317485552, 47.063924090649 ], [ -0.14766123249005, 47.069855194532 ], [ -0.16599081567743, 47.064596744766 ], [ -0.17848280781104, 47.069769863363 ], [ -0.15947412139976, 47.085935164317 ], [ -0.14555986138819, 47.091366857483 ], [ -0.14125473717962, 47.103745079728 ], [ -0.15721241252843, 47.101780345038 ], [ -0.18483829723431, 47.108333434925 ], [ -0.18649707382196, 47.101547033466 ], [ -0.20607508372803, 47.09328538537 ], [ -0.24153284082639, 47.1057275119 ], [ -0.25537525933525, 47.100286155299 ], [ -0.2879249468488, 47.101438124536 ], [ -0.29895654468625, 47.099250384298 ], [ -0.3142510524831, 47.091338121731 ], [ -0.34146522078634, 47.087332841605 ], [ -0.34498093726671, 47.09177095779 ], [ -0.35741871073042, 47.094026201074 ], [ -0.38345929253299, 47.087697613397 ], [ -0.39631090043786, 47.087753100169 ], [ -0.40078196900616, 47.070768258647 ], [ -0.40931478414398, 47.06628937151 ], [ -0.4258514883367, 47.072734496606 ], [ -0.44613773080732, 47.067564764094 ], [ -0.46425258388436, 47.067574344676 ], [ -0.47634088284647, 47.054361886701 ], [ -0.48553786553274, 47.065209021486 ], [ -0.47635021862705, 47.072140351589 ], [ -0.46404370608664, 47.074916254874 ], [ -0.46269595640292, 47.081925505658 ], [ -0.49533635185918, 47.082386236675 ], [ -0.54271238339803, 47.068832410093 ], [ -0.55953163443577, 47.061883133164 ], [ -0.55518423855519, 47.056996280386 ], [ -0.55557809674432, 47.043528672476 ], [ -0.54222220688058, 47.035131514655 ], [ -0.54565906541349, 47.029239134682 ], [ -0.56225950470765, 47.030666495232 ], [ -0.56546953385811, 47.019423404938 ], [ -0.57652952670071, 47.017027138711 ], [ -0.58641245768408, 47.009979798203 ], [ -0.59549628776894, 46.997955261938 ], [ -0.61997935748009, 46.993321083755 ], [ -0.62971403267853, 46.996851004876 ], [ -0.64422463763986, 46.995602969044 ], [ -0.67605096460406, 47.000124265341 ], [ -0.68018770038511, 46.987658683517 ], [ -0.69637193445308, 46.994704963523 ], [ -0.71305373485382, 46.986070538333 ], [ -0.72790285494931, 46.994993345811 ], [ -0.74336287533223, 47.000701964828 ], [ -0.74760195615967, 46.991449795286 ], [ -0.76195094831376, 46.992143526967 ], [ -0.7738784459047, 47.004247669583 ], [ -0.78757295549441, 47.005134613994 ], [ -0.80044123437384, 46.994429249843 ], [ -0.82697025806846, 46.992404409034 ], [ -0.83853635765488, 46.985503699072 ], [ -0.85591689102216, 46.979079840933 ], [ -0.84915622076396, 46.973775579115 ], [ -0.85764337306558, 46.969397597368 ], [ -0.8797290084417, 46.975803771985 ], [ -0.89196408624284, 46.975820414303 ]
                     ]
                  }
              }
          }
        }
      }
   }'
