package tn.fss;

import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class Main {

    public static void main(String[] args) {

        try {
            // ETAPE 4 : Validation XML / XSD
            SchemaFactory factory = SchemaFactory.newInstance(
                    XMLConstants.W3C_XML_SCHEMA_NS_URI);

            try (InputStream xsd =
                         Main.class.getResourceAsStream("/etudiant.xsd");
                 InputStream xml =
                         Main.class.getResourceAsStream("/etudiant.xml")) {

                if (xsd == null || xml == null) {
                    throw new Exception(
                            "Fichier XML ou XSD introuvable !");
                }

                Schema schema = factory.newSchema(
                        new StreamSource(xsd));

                Validator validator = schema.newValidator();

                validator.validate(new StreamSource(xml));

                System.out.println("XML valide !");
            }

            // ETAPE 5 : Interrogation XPath
            DocumentBuilderFactory dbFactory =
                    DocumentBuilderFactory.newInstance();

            Document document;

            try (InputStream xml =
                         Main.class.getResourceAsStream("/etudiant.xml")) {

                if (xml == null) {
                    throw new Exception("etudiant.xml introuvable !");
                }

                document = dbFactory.newDocumentBuilder().parse(xml);
            }

            XPath xpath = XPathFactory.newInstance().newXPath();

            // Requete 1 : tous les etudiants
            XPathExpression exp1 = xpath.compile("//etudiant");
            NodeList resultat1 = (NodeList)
                    exp1.evaluate(document,
                            javax.xml.xpath.XPathConstants.NODESET);

            System.out.println("\nTous les etudiants :");

            for (int i = 0; i < resultat1.getLength(); i++) {
                System.out.println(
                        resultat1.item(i).getTextContent());
            }

            // Requete 2 : etudiants de la filiere LSI
            XPathExpression exp2 =
                    xpath.compile("//etudiant[filiere='LSI']");

            NodeList resultat2 = (NodeList)
                    exp2.evaluate(document,
                            javax.xml.xpath.XPathConstants.NODESET);

            System.out.println("\nEtudiants de la filiere LSI :");

            for (int i = 0; i < resultat2.getLength(); i++) {
                String nom = resultat2.item(i)
                        .getChildNodes().item(0).getTextContent();

                System.out.println(resultat2.item(i)
                        .getTextContent());
            }

            // Requete 3 : moyenne superieure ou egale a 15
            XPathExpression exp3 =
                    xpath.compile("//etudiant[moyenne >= 15]");

            NodeList resultat3 = (NodeList)
                    exp3.evaluate(document,
                            javax.xml.xpath.XPathConstants.NODESET);

            System.out.println(
                    "\nEtudiants ayant une moyenne >= 15 :");

            for (int i = 0; i < resultat3.getLength(); i++) {
                System.out.println(
                        resultat3.item(i).getTextContent());
            }

            // Requete 4 : recherche par identifiant
            XPathExpression exp4 =
                    xpath.compile("//etudiant[@id='E001']");

            NodeList resultat4 = (NodeList)
                    exp4.evaluate(document,
                            javax.xml.xpath.XPathConstants.NODESET);

            System.out.println("\nEtudiant avec l'identifiant E001 :");

            for (int i = 0; i < resultat4.getLength(); i++) {
                System.out.println(
                        resultat4.item(i).getTextContent());
            }

        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }
}