import sys, os
from rdkit import Chem

class FileParser:

    current_mol = Chem.Mol()
    smilefiles = list()
    input_dir = str()
    before_function = None
    after_function = None
    parsing_function = None

    def __init__(self, parsing_method, input_dir, before=None, after=None):
        if before:
            self.before_function = before
        if after:
            self.after_function = after
        self.parsing_function = parsing_method
        self.input_dir = input_dir
        files = [x for x in os.listdir(input_dir)]
        files.sort()
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
                        self.current_mol = mol
                        self.parsing_function(mol, path=smilefile_path)
                    else:
                        sys.stderr.write("Molecule #{0} ({1}) at {2} FAILED to parse.\n".format(number, smiles, smilefile_path))

        if self.after_function:
            self.after_function(input_dir=self.input_dir)