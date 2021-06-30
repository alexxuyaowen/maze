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

def main():
    inp = input("Enter code/text: ")
    if inp == '#' or inp == '':
        exit()
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