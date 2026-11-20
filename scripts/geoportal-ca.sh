#!/usr/bin/env bash
# v133 (pkt 424): mapy.geoportal.gov.pl – Android nie ufa łańcuchowi certyfikatów Geoportalu („Trust anchor for
# certification path not found”; serwer nie wysyła pośredniego certyfikatu albo wystawca nie jest w magazynie Androida).
# Przy budowaniu pobieramy łańcuch (z serwera + brakujące ogniwa z adresów „CA Issuers” w certyfikatach) i zapisujemy
# CERTYFIKATY WYSTAWCÓW (bez certyfikatu serwera) jako dodatkowe zaufane tylko dla domeny mapy.geoportal.gov.pl
# (res/xml/network_security_config.xml). Inne domeny – wyłącznie systemowy magazyn Androida.
# Gdy pobranie się nie uda – konfiguracja bez dodatku (jak dotąd), budowanie idzie dalej.
set -u
HOST=${GEO_HOST:-mapy.geoportal.gov.pl}
RES=android/app/src/main/res
OUT=$RES/raw/geoportal_ca.pem
XML=$RES/xml/network_security_config.xml
W=$(mktemp -d)
mkdir -p "$RES/raw" "$RES/xml"
base_xml() {
  cat > "$XML" <<X
<?xml version="1.0" encoding="utf-8"?>
<!-- OMM: tylko https, systemowe certyfikaty$1 -->
<network-security-config>
    <base-config cleartextTrafficPermitted="false">
        <trust-anchors>
            <certificates src="system" />
        </trust-anchors>
    </base-config>$2
</network-security-config>
X
}
ok=0
if timeout 25 openssl s_client ${OSSL_PROXY:+-proxy "$OSSL_PROXY"} -showcerts -servername "$HOST" -connect "$HOST:443" </dev/null >"$W/s.txt" 2>/dev/null; then
  awk '/BEGIN CERTIFICATE/{n++; on=1} on{print > "'"$W"'/srv" n ".pem"} /END CERTIFICATE/{on=0}' "$W/s.txt"
  ls "$W"/srv*.pem >/dev/null 2>&1 && ok=1
fi
if [ $ok = 1 ]; then
  : > "$W/ca.pem"
  # wystawcy przysłani przez serwer (wszystko poza pierwszym = certyfikatem serwera)
  for f in $(ls "$W"/srv*.pem | sort -V | tail -n +2); do cat "$f" >> "$W/ca.pem"; done
  # brakujące ogniwa: idziemy w górę po „CA Issuers” (najwyżej 4 poziomy)
  cur=$(ls "$W"/srv*.pem | sort -V | tail -1)
  for i in 1 2 3 4; do
    subj=$(openssl x509 -in "$cur" -noout -subject 2>/dev/null)
    iss=$(openssl x509 -in "$cur" -noout -issuer 2>/dev/null)
    [ "${subj#subject=}" = "${iss#issuer=}" ] && break
    url=$(openssl x509 -in "$cur" -noout -text 2>/dev/null | grep -m1 'CA Issuers - URI:' | sed 's/.*URI://')
    [ -n "$url" ] || break
    curl -fsSL --max-time 20 "$url" -o "$W/i$i.bin" || break
    openssl x509 -inform DER -in "$W/i$i.bin" -out "$W/i$i.pem" 2>/dev/null || openssl x509 -in "$W/i$i.bin" -out "$W/i$i.pem" 2>/dev/null || break
    cat "$W/i$i.pem" >> "$W/ca.pem"
    cur="$W/i$i.pem"
  done
  if grep -q 'BEGIN CERTIFICATE' "$W/ca.pem"; then
    cp "$W/ca.pem" "$OUT"
    echo "Geoportal: dodatkowe certyfikaty wystawców:"
    awk '/BEGIN/{n++; on=1} on{print > "'"$W"'/o" n ".pem"} /END/{on=0}' "$OUT"
    for f in "$W"/o*.pem; do openssl x509 -in "$f" -noout -subject -enddate; done
    base_xml ", dla mapy.geoportal.gov.pl dodatkowo wystawcy pobrani przy budowaniu (scripts/geoportal-ca.sh)" "
    <domain-config cleartextTrafficPermitted=\"false\">
        <domain includeSubdomains=\"false\">$HOST</domain>
        <trust-anchors>
            <certificates src=\"system\" />
            <certificates src=\"@raw/geoportal_ca\" />
        </trust-anchors>
    </domain-config>"
    exit 0
  fi
fi
echo "::warning::Geoportal: nie udało się pobrać łańcucha certyfikatów – konfiguracja bez dodatku"
rm -f "$OUT"
base_xml "" ""
exit 0
