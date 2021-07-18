import random

cards = []
players = []
com_cards = []
major_suit = None

ranks = ['2', '3', '4', '5', '6', '7', '8', '9', '10', 'J', 'Q', 'K', 'A']
suits = ["Spade", "Club", "Diamond", "Heart"]
hands = ["High Card", "One Pair", "Two Pair", "Three of a Kind", "Straight", "Flush", "Full House", "Four of a Kind", "Straight Flush", "Royal Flush"]

for i in range(52):
    cards.append((suits[i//13], ranks[i%13]))

def deal(n=2):
    players.clear()
    com_cards.clear()

    n52 = list(range(52))
    random.shuffle(n52)

    for i in range(5):
        com_cards.append(cards[n52[i]])

    global major_suit
    major_suit = majorSuit()

    for i in range(5, n*2+5, 2):
        players.append([cards[n52[i]], cards[n52[i+1]]])

    print("Community Cards: ", com_cards, '\n')

    hs = []
    for i in range(n):
        p = players[i]
        print("Player " + str(i) + ":", p)
        hs.append(bestCombo(p))
        print(hs[i][0] + ":", str(hs[i][1]), '\n')

    winners = winner(hs)
    l = len(winners)

    if l == n:
       print("It's a draw.")

    elif l == 1:
        w = winners[0]
        print("Player", w, "wins with", str(hs[w][0]) + "!\n")
    elif l == 2:
        w1 = winners[0]
        w2 = winners[1]
        print("Player", w1, "and Player", w2, "both win with a", str(hs[w1][0]) + "!\n")
    else:
        print(hs[winners[0]][0], "wins.")
        print("Winners:")
        for w in winners:
            print("Player", w)
        print()

#return the winner
def winner(hs):
    if len(players) < 2:
        return

    best_hand = bestHand(hs)
    best_hands = [h for h in hs if h[0] == best_hand]

    n = len(best_hands)

    if n == 1:
        return [i for i in range(len(players)) if hs[i][0] == best_hand]

    best = better(best_hands[0], best_hands[1])
    for i in range(2, n):
        best = better(best, best_hands[i])

    return [i for i in range(len(players)) if equal(hs[i], best)]

#return the best hand from best combinations
def bestHand(combos):
    return hands[max([hands.index(c[0]) for c in combos])]

#Given two hole cards, return the best combination it can make with the 5 community cards
def bestCombo(h):
    combo = h + com_cards
    flush = bestFlush(combo)
    dup = duplicates(combo)
    straight = bestStraight(combo)

    if flush != None:
        #Straight Flush
        if flush[1] == True:
            #Royal!
            if flush[0][0][1] == 'A':
                return (hands[9], flush[0])
            else:
                return (hands[8], flush[0])

        #Flush
        if dup[1] < 6:
            return (hands[5], flush[0])

    #Straight
    if straight != None and dup[1] < 6:
        return (hands[4], straight)

    return (hands[dup[1]], dup[0])


#If the community cards contain more than 3 suited cards, then there is a major suit; there could be no more than one major suit
def majorSuit():
    com_suits = [card[0] for card in com_cards]

    for s in suits:
        if com_suits.count(s) >= 3:
            return s

    return None

def bestFlush(combo):
    if major_suit == None:
        return None

    flush = [card for card in combo if card[0] == major_suit]

    if len(flush) < 5:
        return None

    s = bestStraight(flush)
    if s != None:
        return (s, True)

    return (highCards(flush), False)

#Return the best straight from a combo of cards
def bestStraight(combo):
    pure_ranks = []
    dict = {}

    for card in combo:
        r = getRank(card)
        if r not in pure_ranks:
            pure_ranks.append(r)
            dict.update({r:card})

    pure_ranks.sort()

    if pure_ranks[-1] == 12:
        pure_ranks.insert(0, -1)
        dict.update({-1:dict.get(12)})

    i = len(pure_ranks)-1
    while i-4 >= 0:
        if pure_ranks[i] - pure_ranks[i-4] == 4:
            pure_ranks = pure_ranks[i-4:i+1]
            result = [dict.get(r) for r in pure_ranks]
            result = result[::-1]
            return result

        i -= 1

    return None

def duplicates(combo):
    result = []
    counts = [0] * 13

    for card in combo:
        counts[getRank(card)] += 1

    max_count = max(counts)

    #Four of A Kind
    if max_count == 4:
        major_rank = counts.index(4)
        result = [card for card in combo if getRank(card) == major_rank]
        rest = [card for card in combo if card not in result]
        result.append(getBiggestCard(rest))
        return (result, 7)

    elif max_count == 3:
        major_rank = 0
        for i in range(13):
            if counts[i] == 3:
                major_rank = max(major_rank, i)

        result = [card for card in combo if getRank(card) == major_rank]
        rest = [card for card in combo if card not in result]

        #Full House
        if counts.count(2) >= 1:
            minor_pair = getBiggestPair(rest)
            result += [card for card in rest if getRank(card) == minor_pair]
            return (result, 6)

        #Three of A Kind
        result += highCards(rest)[:2]

        return (result, 3)

    elif max_count == 2:
        major_pair = getBiggestPair(combo)
        result = [card for card in combo if getRank(card) == major_pair]
        rest = [card for card in combo if card not in result]

        #Two Pair
        if counts.count(2) >= 2:
            minor_pair = getBiggestPair(rest)
            result += [card for card in combo if getRank(card) == minor_pair]
            rest = [card for card in combo if card not in result]

            big_card = getBiggestCard(rest)
            result.append(big_card)

            return (result, 2)

        #One Pair
        result += highCards(rest)[:3]

        return (result, 1)

    return (highCards(combo), 0)

#Find 5 highest cards from a combination of cards which has no duplicates
def highCards(combo):
    return [c[1] for c in sorted([(getRank(card), card) for card in combo], reverse=True)][:5]

#Return the biggest card in combo which has potential duplicates
def getBiggestCard(combo):
    return max([(getRank(card), card) for card in combo])[1]

#Return the rank of the biggest pair in combo
def getBiggestPair(combo):
    pure_ranks = sorted([getRank(card) for card in combo], reverse=True)

    for i in range(len(pure_ranks)-1):
        if pure_ranks[i] == pure_ranks[i+1]:
            return pure_ranks[i]

    return None

def getSuit(card):
    return suits.index(card[0])

def getRank(card):
    return ranks.index(card[1])

def getID(card):
    return getSuit(card) * 13 + getRank(card)

#format decimals
def toPercent(p, precision=3):
    f = "{:."+str(precision)+"%}"

    return f.format(p)
    # return str(round(p*100, precision)) + "%"

#Generate all 169 different starting hands
def allHands():
    list = []

    for i in range(13):
        card1 = cards[i]

        for j in range(i+1,13):
            list.append([card1, cards[j]])

        for j in range(13+i, 26):
            list.append([card1, cards[j]])

    return list

#compare two hands of same degree, which should be sorted already
def better(hand1, hand2):
    cards1 = hand1[1]
    cards2 = hand2[1]

    for i in range(5):
        p1 = getRank(cards1[i])
        p2 = getRank(cards2[i])

        if p1 > p2:
            return hand1
        elif p1 < p2:
            return hand2

    return hand1

#check if two hands are basically the same, regardless of difference suits
def equal(hand1, hand2):
    if hand1[0] != hand2[0]:
        return False

    cards1 = hand1[1]
    cards2 = hand2[1]

    for i in range(5):
        if getRank(cards1[i]) != getRank(cards2[i]):
            return False

    return True

#Given a starting hand, the number of players, and number of rounds to play, return the probability of not losing if all-in pre-flop
def getWinningRate(hand, board=[], num_players=2, num_rounds=33333):
    global players
    players.clear()
    players.append(hand)

    card1 = hand[0]
    card2 = hand[1]
    n52 = list(range(52))
    n52.remove(getID(card1))
    n52.remove(getID(card2))

    for c in board:
        n52.remove(getID(c))

    count = 0

    for _ in range(num_rounds):
        players = players[:1]

        global com_cards
        com_cards.clear()
        random.shuffle(n52)
        com_cards = board[:]

        for i in range(len(board), 5):
            com_cards.append(cards[n52[i]])

        global major_suit
        major_suit = majorSuit()

        for i in range(5, num_players*2+3, 2):
            players.append([cards[n52[i]], cards[n52[i+1]]])

        hs = [bestCombo(p) for p in players]
        winners = winner(hs)

        if 0 in winners:
            count += 1

    return count/num_rounds

def compete(hand1, hand2, num_rounds=100000):
    global players
    players.clear()
    players.append(hand1)
    players.append(hand2)

    n52 = list(range(52))
    n52.remove(getID(hand1[0]))
    n52.remove(getID(hand1[1]))
    n52.remove(getID(hand2[0]))
    n52.remove(getID(hand2[1]))

    count = 0
    draw_count = 0

    for _ in range(num_rounds):
        com_cards.clear()

        random.shuffle(n52)

        for i in range(5):
            com_cards.append(cards[n52[i]])

        global major_suit
        major_suit = majorSuit()

        hs = [bestCombo(p) for p in players]
        winners = winner(hs)
        if len(winners) == 2:
            draw_count += 1
        elif 0 in winners:
            count += 1

    return (count/num_rounds, draw_count/num_rounds)


def ranking(num_players=6, num_rounds=33333):
    list = allHands()
    result = []

    for h in list:
        value = getWinningRate(h, [], num_players, num_rounds)
        result.append((value, h))

    result.sort(reverse=True)
    print("For a game consists of", num_players, "players:")
    print("".ljust(13), "Hand".ljust(24), "Winning Rate")

    for x in result:
        print(x[1], "\t", toPercent(x[0]))

    print()

#Given a board, rank each hand by their winrate
def handRanking(board, num_players=6, num_tests=33333):
    return

def competeAgainst(num_rounds=33333):
    hand_inp = input("Your hand: ")
    hand_inp = hand_inp.upper().split()
    hand = []

    for s in hand_inp:
        if s[0] in ['S', 'C']:
            print("Incorrect Input: Please use cards of Heart or Diamond.\n")
            return competeAgainst()

        hand.append(toCard(s))

    hs = allHands()

    result = []

    for h in hs:
        value = compete(h, hand, num_rounds)
        result.append((value[0], value[1], h))

    result.sort(reverse=True)

    print("=> Competing against", hand)
    print("".ljust(13), "Hand".ljust(24), "Winning Rate".ljust(26), "Draw")

    for x in result:
        print(x[2], "\t", toPercent(x[0]), "\t\t", toPercent(x[1]))

    print()

def toCard(s):
    dict = {'C': "Club", 'D': "Diamond", 'H': "Heart", 'S': "Spade"}

    return (dict.get(s[0]), s[1:])

def run():
    import time
    t0 = time.time()


    ###deal()
    # deal(9)

    ###statistics()
    # statistics(1, 1000000)
    # statistics(9, 100000)

    ###getWinningRate()
    # hand = [toCard("HA"), toCard("H10")]
    # board = []
    # num_players = 9
    # win = getWinningRate(hand, board, num_players)

    # print("Number of players:", num_players)
    # print("Your hand:", hand)
    # print("The board:", board)
    # print("Your chance of winning:", toPercent(win))

    # if win > 1/2:
        # print("=> All in.")
    # elif win > 1/7:
        # for x in range(3, 8):
            # if win > 1/x:
                # print("Maximum bet: 1/" + str(x-1), "of the pot.")
                # break
    # else:
        # print("=> Check/Fold.")

    ##compete()
    # hand1 = [("Heart", 'A'), ("Heart", 'K')]
    # hand2 = [("Club", '4'), ("Club", 'J')]
    # num_rounds = 100000

    # print("In", num_rounds, "duels:")
    # result = compete(hand1, hand2, num_rounds)
    # print(hand1, "beats", hand2, "\n" + toPercent(result[0]), "of the time, with a probablity of", toPercent(result[1]), "having a draw.\n")


    ### ranking()
    # ranking(5)
    # ranking(6)
    # ranking(9)


    ###competeAgainst() !!! The chosen cards must have suits of Diamonds or Hearts !!!
    # hand = [("Diamond", 'J'), ("Heart", 'J')]
    # competeAgainst(hand)

    ##analysis()
    # analysis(hand)

    t = round((time.time() - t0) / 60)
    if t >= 2:
        print("Total running time:", t, "minutes.")

def advice():
    hand_inp = input("Your hand: ")
    board_inp = input("The board: ")
    hand_inp = hand_inp.upper().split()
    board_inp = board_inp.upper().split()
    hand, board = [], []

    for s in hand_inp:
        hand.append(toCard(s))

    for s in board_inp:
        board.append(toCard(s))

    phase = len(board)

    num_players = int(input("Number of players: "))

    winrate = getWinningRate(hand, board, num_players)

    print()

    if phase == 0:
        print("@Pre-Flop")
    elif phase == 3:
        print("@Flop")
    elif phase == 4:
        print("@Turn")
    else:
        print("@River")

    print("For a game consists of", num_players, "players:")

    if phase != 0:
        print("Given the board:", board)
    print("With hand", hand)

    print("Your chance of winning is", toPercent(winrate))


    if phase == 0:
        threshold = 1/num_players

        #All-in with the best possible hands
        if winrate > threshold + 0.16:
            print("=> Raise/All in")
        elif winrate > threshold + 0.08:
            print("=> Raise")
        elif winrate > threshold + 0.04:
            print("=> Check")
        elif winrate > threshold:
            print("=> Check/Fold.")
        else:
            print("=> Fold.")

    else:
        #80% chance of winning is about all-in with QQ pre-flop against a random player
        #80% chance of winning is also about the winrate of AA against KK
        #80% chance of winning is also about flopping a 22233 full house on a full ring table
        if winrate > 4/5:
            print("=> Check-Raise/All in.")
        elif winrate > 1/2:
            print("=> Optimum Bet: a pot.")
        elif winrate > 1/3:
            print("=> Optimum Bet: 1/2 of the pot.")
        # elif winrate > 1/4:
            # print("=> Optimum Bet: 1/3 of the pot.")
        else:
            print("=> Fold/Bluff.")


        # if winrate > 4/5:
            # print("=> Check-Raise/All in")
        # else:
            # print("Maximum bet:", toPercent(1/(1/winrate-1), 0), "of the pot.")

    print()
    advice()

def dual():
    hand1_inp = input("hand1: ")
    hand2_inp = input("hand2: ")

    hand1_inp = hand1_inp.upper().split()
    hand2_inp = hand2_inp.upper().split()

    hand1, hand2 =[], []

    for s in hand1_inp:
        hand1.append(toCard(s))

    for s in hand2_inp:
        hand2.append(toCard(s))

    num_rounds = 100000

    print()

    print("In", num_rounds, "duels:")
    result = compete(hand1, hand2, num_rounds)
    print(hand1, "beats", hand2, "\n" + toPercent(result[0]), "of the time, with a probablity of", toPercent(result[1]), "having a draw.\n")

def analysis(num_tests=100000):
    hand_inp = input("Your hand: ")
    hand_inp = hand_inp.upper().split()
    hand = []

    for s in hand_inp:
        hand.append(toCard(s))

    print("\n=> With", str(hand) + ":")

    n52 = list(range(52))
    n52.remove(getID(hand[0]))
    n52.remove(getID(hand[1]))

    h = []

    for _ in range(num_tests):
        com_cards.clear()
        random.shuffle(n52)
        for i in range(5):
            com_cards.append(cards[n52[i]])

        global major_suit
        major_suit = majorSuit()

        h.append(bestCombo(hand)[0])


    for x in hands:
        print("P("+x+ ") =", toPercent(h.count(x) / num_tests))

    print()

def statistics(num_tests=33333):
    num_players = int(input("Number of players: "))
    board_inp = input("The board: ")
    board_inp = board_inp.upper().split()
    board = []
    auto_fill = 'N'

    for s in board_inp:
        board.append(toCard(s))

    n52 = list(range(52))
    record = []

    for c in board:
        n52.remove(getID(c))

    phase = len(board)

    if phase < 5:
        auto_fill = input("Auto-fill the board? ").upper()

    for _ in range(num_tests):
        players.clear()

        global com_cards
        com_cards.clear()
        random.shuffle(n52)
        com_cards = board[:]

        if auto_fill == 'Y':
            for i in range(phase, 5):
                com_cards.append(cards[n52[i]])

        global major_suit
        major_suit = majorSuit()

        for i in range(5, 5+num_players*2, 2):
            players.append([cards[n52[i]], cards[n52[i+1]]])

        combos = [bestCombo(p) for p in players]

        record.append(bestHand(combos))

    print()

    if phase == 0:
        print("@Pre-Flop")
    elif phase == 3:
        print("@Flop")
    elif phase == 4:
        print("@Turn")
    else:
        print("@River")

    if num_players > 1:
        print("For a game consists of", num_players, "players:")
        if phase != 0:
            print("Given the board:", board)
        for h in hands:
            prob = record.count(h) / num_tests

            if prob != 0:
                if auto_fill != 'Y':
                    print("P(Best hand is "+h+") =", toPercent(prob))
                else:
                    print("P("+h+" will win) =", toPercent(prob))
    else:
        if phase != 0:
            print("Given the board:", board)
        for h in hands:
            prob = record.count(h) / num_tests

            if prob != 0:
                print("P("+h+") =", toPercent(prob))

    print()

    # statistics()

def menu():
    return

advice()
