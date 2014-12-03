import sys, os
from rdkit import Chem

class FileParser:

    # current_mol = Chem.Mol()
    # smilefiles = list()
    # input_dir = str()

    def __init__(self, parsing_method, input_dir, args=[], before=None, after=None, ret_function=None):
        self.before_function = before
        self.after_function = after
        self.ret_function = ret_function
        self.parsing_function = parsing_method
        self.parsing_function.args = args
        self.input_dir = input_dir
        files = [x for x in os.listdir(input_dir)]
        files.sort()
        self.smilefiles = []
        for infile in files:
            if infile.endswith(".smi"):
                self.smilefiles.append(os.path.join(input_dir, infile))

    def run(self):
        if self.before_function:
            self.before_function(input_dir=self.input_dir)

        for smilefile_path in self.smilefiles:
            with open(smilefile_path, mode="r") as infile:
                for line in infile:
                    data = line.split("\t")
                    smiles = data[0].strip()
                    number = data[1].strip()
                    mol = Chem.MolFromSmiles(smiles)
                    if mol:
                        mol.SetProp("_Name", number)
                        # self.current_mol = mol
                        if self.ret_function:
                            self.ret_function(self.parsing_function(mol, path=smilefile_path))
                        else:
                            self.parsing_function(mol, path=smilefile_path)
                    else:
                        sys.stderr.write("Molecule #{0} - {1} - at {2} FAILED to parse.\n".format(number, smiles, smilefile_path))
            print "FINISHED PROCESSING FILE: " + smilefile_path

        if self.after_function:
            self.after_function(input_dir=self.input_dir)