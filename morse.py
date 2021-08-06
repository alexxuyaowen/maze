t = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 ,.':?!"
c = [".-","-...","-.-.","-..",".","..-.","--.","....","..",".---","-.-",".-..","--","-.","---",".--.","--.-",".-.","...","-","..-","...-",".--","-..-",\
    "-.--","--..", "-----",".----","..---","...--","....-",".....","-....","--...","---..","----.", " ", "--..--", ".-.-.-",".----.","---...","..--..","-.-.--"]

to_code = dict(zip(t,c))
to_text = dict(zip(c,t))
to_text.update({'#':' '})

def encode(t):
    result = ""
    for c in t.upper():
        result += to_code.get(c)
        result += " "

    return result[:-1]

def decode(c):
    codes = c.replace("  ", " # ")
    codes = codes.split()

    result = ""
    for c in codes:
        result += to_text.get(c)

    return result

def practice():
    print("0. Practice code reading")
    print("1. Practice translation to codes")

    choice = input("Your choice: ")
    print()

    import random
    import time
    display = ""
    level = 4

    if choice == '0':
        for _ in range(2**level-1):
            display += c[random.randint(0,25)] + " "
        display += c[random.randint(0,25)]
        print(display)
        start = time.time()

        user_inp = input("Enter text: ").upper()
        answer = decode(display)

        # make sure that user_inp has the correct length
        diff = 2**level - len(user_inp)
        if diff > 0:
            user_inp += '#' * diff

        if user_inp == answer:
            print("Correct!")
        else:
            hint = ""
            for i in range(2**level):
                correct = answer[i]
                if user_inp[i].upper() == correct:
                    hint += '+'
                else:
                    hint += correct
            print("  Answer  :", hint)

        end = time.time()
        print("Time spent:", "{:.2f}".format(end-start), "seconds.")

    elif choice == '1':
        for _ in range(2**level):
            display += t[random.randint(0,25)]
        print(display)
        start = time.time()

        user_inp = input("Enter codes: ")
        answer = encode(display)

        if user_inp == answer:
            print("Correct!")
        else:
            print("  Answer   :", answer)

        end = time.time()
        print("Time spent:", "{:.2f}".format(end-start), "seconds.")

    else:
        print("error: invalid choice")

    choice = input("\nContinue? [Y/N]\n")
    if choice == '-.--' or choice.upper() == 'Y' or choice.upper() == "YES":
        print()
        practice()
    else:
        return

def main():
    inp = input("Enter code/text: ")
    if inp == '#' or inp == '':
        exit()
    elif inp.lower() == "#p" or inp.lower() == "#practice":
        practice()
    elif inp.strip(".- ") == "":
        try:
            print("=>", decode(inp))
        except TypeError:
            print("!Error: Unsupported Code Detected.")
    else:
        try:
            print("=>", encode(inp))
        except TypeError:
            print("!Error: Unsupported Symbol Detected.")

    print()

    # inp = input("Continue? [Y/N]\n")
    # if inp == '-.--' or inp.upper() == 'Y' or inp.upper() == "YES":
        # print()
        # print()
        # print()
    main()

print("Welcome!")
print("----------------------------------------")
main()
