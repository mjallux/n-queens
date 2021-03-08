# Aleksi Alajärvi
##  N Kuningattaren ongelma
## Toteus Dijkstran vetäytymisalgoritmilla.

<br>

# 1. Yleiskatsaus

N Kuningattaren ongelmassa tulee asettaa N määrä kuningattaria N*N shakkilaudalle siten, että ne eivät uhkaa toisiaan.

Dijkstran algoritmi toimii seuraavasti:

1. Etsi seuraava ruutu, johon voi asettaa kuningatar.

2. Jos kuningattaria on laudalla N kappaletta, lisää tuloksiin 1. Tulokset ovat mahdollisia shakkilaudan asetelmia joissa on N kuningatarta.

3. Jos seuraavaa kuningatarta ei voi asettaa laudalle ja kuningattaria on laudalla vähemmän kuin N, peruuta siirtoja kunnes voit asettaa kuningattaren taas laudalle käyttämättä jo tutkittuja laudan asetelmia.

4. Toista kunnes kuningattaria ei voi asettaa laudalle enään lainkaan.

<br>

# 2. Toteutus

### Datastruktuurit

Lauta: N*N BitSet

Kuningattaret: K-ary tree

Lautaa käytetään ainoastaan seuraavien mahdollisten siirtojen laskemiseen.

Lisäksi ohjelman alussa generoidaan N\*N määrä N\*N kokoisia taulukoita, joiden avulla lasketaan bittioperaatioilla seuraava mahdollinen siirto. 

Bittioperaatioilla eliminoidaan ylimääräinenn iterointi jokaista laudan kuningatarta kohti.

```
lauta  OR   maski   =   lauta  OR   maski   =   lauta -->
x0000       x1111       x0000       x1111       x0000
00000       11000       00x00       11x11       00x00
00000       10100       00000       11110       0000x
00000       10010       00000       10111       00000
00000       10001       00000       10101       00000
```

Puuhun tallennetaan:
- Kunigattaren x & y paikat
- Rivin mahdolliset siirrot
  - N pitkä BitSet, jokainen indeksi on mahdollinen x positio (0 jos siirto mahdollinen, 1 jos ruutu on uhattu)
- Haaran lapset (children)
- Haaran isäntä (parent)

<img src="https://upload.wikimedia.org/wikipedia/en/e/e8/Ternary_tree_2.png" width="75%"></img>
> K-ary tree visualisaatio
>
> CC BY 4.0, https://en.wikipedia.org/w/index.php?curid=60823732


<br>

---

### Algoritmi

---

Kirjoitan pseudokoodilla perustoimintaperiaatteen.

Siirtomaskien generointi


```
BitSet[] generateMasks():
    masks = new BitSet[N*N]

    while(N*N kertaa):
        mask = new BitSet(N*N)
        
        mask.set.rows
        mask.set.cols
        mask.set.diagonals

        masks.push(mask)

    return masks
```

Rekursiivinen backtrack algoritmi


```
dijkstra(rootNode) ->


dijkstra(node):
    if(board.queens === N):
        solutions++

    possibleMoves = board.forEach(lambda(queen) -> board.or(masks[i]))
    
    move = possibleMoves.first
    
    if(move):
        board.set(move)

        nextNode = new Node(move)
        node.addChild(node)
        node.moves.set(curPossibleMoves)

        return dijkstra(netxNode)
    
    else:
        if(node === rootNode && board.queens === N):
            console.log(solutions)
            return

        board.unset(lastMove)
        node.parent.setExplored(this)

        return dijkstra(node.parent)
```

---

### Parannettavaa:
- Monisäikeistys, tällä hetkellä ohjelma on vain yksisäikeinen.
- Java toteutuksessani on paljon ohjelmahaaroja, jotka voisi todennäköisesti saada pois.
- Branchless programming?
- Testaa BitSet vs bool[] nopeus? boolean[8][8] veisi 512 bittiä muistia, kun taas BitSet(N*N) vie tasan 64 bittiä. Operaatiot saattavat olla nopeampia booleanille?
- Piirrä hienompi shakkilauta
- Piirrä kuva puusta, helppo visualisoida. Kaikki puun uloimmat haarat/lehdet ovat N kuningattaren ratkaisuja.
