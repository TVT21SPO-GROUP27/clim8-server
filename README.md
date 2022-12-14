# 🌍 | clim8

Clim8 on Oulun ammattikorkeakoulun 2. vuoden ohjelmistokehityksen opiskelijoiden luoma sovellus joka visualisoi ilmastonmuutosta. 

Projektin toteuttamiseen tarvittiin Server ja Client. Server-puoli hoitaa tietokannan ja tiedon haun. Client-puoli hoitaa sovelluksen käyttöliittymän ja tiedon näyttämisen käyttäjälle.  

Asiakas voi käyttää sovellusta katsoakseen valmiiksi tehtyjä kaavioita ja käyttää niiden ominaisuuksia. Halutessaan asiakas voi luoda käyttäjän, jolloin hän pystyy itse luomaan erilaisia kaavioita ja tallettamaan niitä. Käyttäjä voi myös jakaa luomansa kaavion linkkinä. Jaetun kaavion avaamiseen ei tarvitse luoda käyttäjää. 

## Teknologiat 

Client-puoli luotiin käyttämällä React Frameworkia ja ohjelmointikielenä käytettiin JavaScriptiä. Server –puoli luotiin käyttämällä Spring Boot Frameworkia ja Gradle-buildaustyökalua ja ohjelmointikielenä käytettiin Javaa. 

## Tekijät 
Tekijät olivat Niklas Siltala, Timon Poutiainen, Joona Sivonen ja Lassi Riekkola 

## Tietokantarakenne 
<div align='center'>
  <picture>
    <img src='../master/Photos/hadcrut1.png' height='192' alt="Logo">
  </picture>
</div> 
<div align='center'>
  <picture>
    <img src='../master/Photos/hadcrut2.png' height='192' alt="Logo">
  </picture>
</div> 
<div align='center'>
  <picture>
    <img src='../master/Photos/hadcrut3.png' height='192' alt="Logo">
  </picture>
</div> 
<div align='center'>
  <picture>
    <img src='../master/Photos/localdata.png' height='192' alt="Logo">
  </picture>
</div> 

Tietokanta toimii siten, että se käy suoraan internetistä lukemassa CSV- tai tekstitiedoston, missä ilmastonmuutoksen tutkimusdata sijaitsee. Tämän jälkeen se luo tiedoston localdata.db, johon se tallettaa tiedot. Tällaisella toteutus tavalla tietokannan tietoja ei tarvitse itse käsin kirjoittaa tietokantaan. Kun käynnistää Serverin ensimmäistä kertaa, siinä menee pitempään juurikin sen takia, koska sen pitää hakea internetistä ensin tiedot tietokantaan. Osa tiedoista on kuitenkin “kovakoodattu” tietokantaan koska ihan kaikkea tutkimustyön dataa ei ollut saatavilla teksti –tai csv –tiedostona. 

## Github & Sovellus -linkki:  

https://github.com/TVT21SPO-GROUP27 

http://www.clim8.fi/ 
