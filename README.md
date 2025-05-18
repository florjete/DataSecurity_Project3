# Secure file transfer protocol me Kriptografi Hibride

Ky projekt implementon një **Secure file transfer protocol** duke përdorur një qasje hibride kriptografike për të ngarkuar dhe shkarkuar skedarë në mënyrë të sigurt midis klientit dhe serverit. Protokolli përdor kombinimin e **RSA (2048-bit)** për enkriptim asimetrik dhe **AES (256-bit)** për enkriptim simetrik për të siguruar konfidencialitetin dhe integritetin gjatë transferimit të të dhënave.

## Karakteristikat:

* Ngarkim dhe shkarkim i sigurt i skedarëve
* Enkriptim me RSA për shkëmbim çelësash
* Enkriptim me AES për të dhënat e skedarëve
* Verifikim i nënshkrimeve dixhitale për integritet të të dhënave
* Ruajtje e sigurt e çelësave të enkriptuar
* Trajtim i gabimeve dhe logje për debugging

---

## Struktura e Projektit:

```
src/
│
├── client/
│   ├── Client.java             # Aplikacioni kryesor i klientit
│   ├── ClientHandler.java      # Menaxhon logjikën e ngarkimit dhe shkarkimit
│   └── AESUtil.java            # Funksionalitetet për enkriptim/dekriptim me AES
│   ├── RSAUtil.java            # Funksionalitetet për enkriptim/dekriptim me RSA
│   ├── DigitalSignature.java   # Gjenerimi dhe verifikimi i nënshkrimeve dixhitale
│   └── HashUtil.java           # Funksionalitetet e hashing (SHA-256)
│
├── server/
│   ├── Server.java             # Aplikacioni kryesor i serverit
│   ├── ServerHandler.java      # Menaxhon lidhjet e klientëve
│   └── FileStorage.java        # Menaxhon ruajtjen e skedarëve në server
│
├── models/
│   ├── TransferSession.java    # Modeli që përfaqëson një sesion transferimi

```

---

## Udhëzime për Instalimin:

### 1️⃣ **Kompilimi i projektit**

```cmd
cd src
javac server\*.java client\*.java models\*.java
```

### 2️⃣ **Startimi i Serverit**

```cmd
cd src
java server.Server
```
**Porta default është 5050.**  
Nëse dëshironi të përdorni një port tjetër, përdorni formatin e mëposhtëm:  

```cmd
java server.Server <porti>
```

**Shembuj:**  
```cmd
java server.Server 9090       # Përdor portin 9090
java server.Server 8080       # Përdor portin 8080
```

---

## Shembull i Përdorimit:

```cmd
java server.Server         # Porta default 5050
java server.Server 9090    # Porta specifike 9090
java client.Client upload C:\path	oile.txt
java client.Client download file.txt
```

---
### 3️⃣ **Startimi i Klientit**

```cmd
cd src
java client.Client <komanda> <fajlli>
```

### Komandat:

* `upload <file_path>`    - Ngarkon skedarin e specifikuar në server.
* `download <file_name>`  - Shkarkon skedarin e specifikuar nga serveri.

---

## Shembull i Përdorimit:

```cmd
java client.Client upload C:\path\to\file.txt
java client.Client download file.txt
```

---

## Trajtimi i Gabimeve:

* Nëse skedari nuk gjendet, klienti do të shfaqë një mesazh gabimi.
* Nëse lidhja ndërpritet, klienti do të tentojë të rilidhet.
* Gjenerohen logje për çdo transaksion për qëllime auditimi dhe debug.

---

## Aspektet e Sigurisë:

* **RSA (2048-bit)** përdoret për shkëmbimin e çelësave për të transmetuar çelësin AES në mënyrë të sigurt.
* **AES (256-bit)** përdoret për enkriptimin e përmbajtjes së skedarëve gjatë transferimit.
* **Nënshkrime Dixhitale** sigurojnë integritetin dhe autenticitetin e skedarëve të transferuar.
* **SHA-256 Hashing** përdoret për të verifikuar integritetin e skedarëve pas shkarkimit.

---
## Perdorimi i kodit per siguri:
* **Klasa AESUtil** ofron metoda ndihmëse për gjenerimin e çelësave AES, enkriptimin dhe dekriptimin e të dhënave duke përdorur algoritmin AES/GCM/NoPadding, i cili është i njohur për siguri të lartë dhe verifikim të integritetit.
```cmd
// Gjenerimi i çelësit AES
SecretKey aesKey = AESUtil.generateAESKey();

// Enkriptimi i një mesazhi
byte[] encryptedData = AESUtil.encrypt(aesKey.getEncoded(), "Pershendetje Botë!".getBytes());

// Dekriptimi i mesazhit
byte[] decryptedData = AESUtil.decrypt(aesKey.getEncoded(), encryptedData);

// Shfaqja e mesazhit të dekriptuar
System.out.println(new String(decryptedData)); // Rezultati: Pershendetje Botë!
```
* **Klasa DigitalSignature** është një klasë që ofron funksionalitete për nënshkrimin dixhital të të dhënave dhe verifikimin e nënshkrimeve duke përdorur algoritmin SHA-256 me RSA.
```cmd
// Nënshkrimi i të dhënave
byte[] data = "Pershendetje Botë!".getBytes();
byte[] signature = DigitalSignature.sign(data, privateKey);

// Verifikimi i nënshkrimit
boolean isValid = DigitalSignature.verify(data, signature, publicKey);

if (isValid) {
    System.out.println("✅ Nënshkrimi është valid!");
} else {
    System.out.println("❌ Nënshkrimi është i pavlefshëm!");
}
```
* **Klasa KeyManager ** është një klasë që menaxhon gjenerimin dhe ruajtjen e çelësave publik dhe privat (RSA) për serverin.
```cmd
// Krijimi i një instance të KeyManager
KeyManager keyManager = new KeyManager();

// Marrja e çelësave publik dhe privat
PublicKey publicKey = keyManager.getPublicKey();
PrivateKey privateKey = keyManager.getPrivateKey();

```

---
## Autorët:

* **Floridë Suka** 
* **Florjetë Kuka** 
* **Flon Kastrati** 
* **Getoar Hoxha** 

---
