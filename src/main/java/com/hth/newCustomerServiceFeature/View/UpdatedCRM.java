package com.hth.newCustomerServiceFeature.View;

import com.hth.id_card.user_interface.HTH_Frame;
import com.hth.newCustomerServiceFeature.CustomerServiceApp;
import com.hth.newCustomerServiceFeature.DomainModel.CrmLogRecord;
import com.hth.newCustomerServiceFeature.Repository.Repository;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public  class UpdatedCRM extends HTH_Frame  {

    private static final Font HTH_FONT = new Font("Arial", Font.PLAIN, 18);
    private JLabel grpLabel ;

    // Components of the Form
    private Container container;
    private JLabel title;

    private JLabel referenceNumber;
    private JTextField tReferenceNumber;
    private JLabel refNumValidation;
    private JButton pReferenceNumber;
    private JButton bReferenceNumber;

    private JLabel provider;
    private JLabel providerValidation;
    private JComboBox tProvider;
    private JButton pProvider; //p -> pop up

    private JLabel phoneNum;
    private JLabel phoneValidation;
    private JTextField tPhoneNum;

    private JLabel name;
    private JLabel nameValidation;
    private JTextField tFName;
    private JTextField tLName;

    private JLabel companyCallingFrom;
    private JLabel companyValidation;
    private JTextField tCompanyCallingFrom;

    private JLabel memberId;
    private JLabel memberIdValidation;
    private JTextField tMemberId;

    private JLabel claimNumber;
    private JLabel claimNumberValidation;
    private JTextField tClaimNumber;

    private JButton submit;
    private JButton cancel;

    private String providerOrMember[] = {"Provider", "Member"};

    public UpdatedCRM(String title) {
        super(title);

        setReferenceNum();

        setProvider();

        setPhone();

        setFNameLName();

        setCompanyCallingFrom();

        setMember();

        setClaimNum();

        UpdateDocumentListener();

        setVisible(true);
    }

    // get the input  Data to be inserted in the DataBase
    private CrmLogRecord getData() {
        String refNum = tReferenceNumber.getText().trim().toUpperCase();
        String providerOrMember = "P";
        if (tProvider.getSelectedIndex() == 1) {
            providerOrMember = "M";
        }
        String phoneNum = tPhoneNum.getText().trim();
        String fName = tFName.getText().trim().toUpperCase();
        String lName = tLName.getText().trim().toUpperCase();
        String company = tCompanyCallingFrom.getText().trim().toUpperCase();
        String ssn = tMemberId.getText().trim().toUpperCase();
        String claimNum = tClaimNumber.getText().trim().toUpperCase();

        CrmLogRecord record = new CrmLogRecord.Builder()
                .withRefNum(refNum)
                .withClaimType(providerOrMember)
                .withPhoneNum(phoneNum)
                .withFName(fName)
                .withLName(lName)
                .withCompanyName(company)
                .withSSN(ssn)
                .withClaimNum(claimNum)
                .withUser(CustomerServiceApp.user)
                .build();

        return record;
    }

    // checkData before inserting into dataBase
    private boolean checkData() {
        String refNum = tReferenceNumber.getText().trim().toUpperCase();
        String providerOrMember = "P";
        if (tProvider.getSelectedIndex() == 1) {
            providerOrMember = "M";
        }
        String phoneNum = tPhoneNum.getText().trim();
        String fName = tFName.getText().trim().toUpperCase();
        String lName = tLName.getText().trim().toUpperCase();
        String company = tCompanyCallingFrom.getText().trim().toUpperCase();
        String ssn = tMemberId.getText().trim().toUpperCase();
        String claimNum = tClaimNumber.getText().trim().toUpperCase();

        if (refNum.length() != 10) {
            refNumValidation.setVisible(true);
            System.out.println("validation error checkData: refname");
            return false;
        }
        if (ssn.length() != 9) {
            memberIdValidation.setVisible(true);
            System.out.println("validation error checkData: memberid");

            return false;
        }
        if (claimNum.length() != 7) {
            claimNumberValidation.setVisible(true);
            System.out.println("validation error checkData: claimNum");

            return false;
        }
        if (phoneNum.length() != 10) {
            phoneValidation.setVisible(true);
            System.out.println(phoneNum.length() +": UpdatedCRM");
            System.out.println("validation error checkData: phoneNum");

            return false;
        }
        if (fName.length() < 2) {
            nameValidation.setVisible(true);
            System.out.println("validation error checkData: fName");

            return false;
        }
        if (lName.length() < 2) {
            nameValidation.setVisible(true);
            System.out.println("validation error checkData: lName");

            return false;
        }
        if (company.length() < 2) {
            companyValidation.setVisible(true);
            System.out.println("validation error checkData: company");

            return false;
        }

        return true;

    }
    private void setReferenceNum() {
        referenceNumber = new JLabel("Reference Number");
        referenceNumber.setFont(new Font("Arial", Font.PLAIN, 18));
        referenceNumber.setSize(200, 30);
        referenceNumber.setLocation(150, 100);
        container.add(referenceNumber);

        refNumValidation = new JLabel("Invalid");
        refNumValidation.setFont(new Font("Arial", Font.PLAIN, 10));
        refNumValidation.setForeground(Color.RED);
        refNumValidation.setSize(40, 30);
        refNumValidation.setLocation(405, 100);
        refNumValidation.setVisible(false);
        container.add(refNumValidation);

        tReferenceNumber = new JTextField();
        tReferenceNumber.setFont(new Font("Arial", Font.PLAIN, 15));
        tReferenceNumber.setSize(280, 30);
        tReferenceNumber.setLocation(450, 100);
        //tReferenceNumber.setInputVerifier(new RefNumValidation());
        container.add(tReferenceNumber);


        pReferenceNumber = new JButton(" ...");
        pReferenceNumber.setFont(new Font("Arial", Font.PLAIN, 18));
        pReferenceNumber.setSize(40, 35);
        pReferenceNumber.setLocation(732, 100);
        container.add(pReferenceNumber);

        bReferenceNumber = new JButton("Generate");
        bReferenceNumber.setFont(new Font("Arial", Font.PLAIN, 14));
        bReferenceNumber.setSize(78, 35);
        bReferenceNumber.setLocation(772, 100);
        //bReferenceNumber.addActionListener(this);

        bReferenceNumber.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Repository repo = Repository.getInstance("SALAHM");
                Integer reference = repo.generateRefNum();
                tReferenceNumber.setText(reference.toString());
            }
        });
        container.add(bReferenceNumber);
    }

    private void setProvider() {
        provider = new JLabel("Provider Or Member");
        provider.setFont(new Font("Arial", Font.PLAIN, 18));
        provider.setSize(200, 30);
        provider.setLocation(150, 140);
        container.add(provider);

        tProvider = new JComboBox(providerOrMember);
        tProvider.setFont(new Font("Arial", Font.PLAIN, 18));
        tProvider.setSize(340, 30);
        tProvider.setLocation(450, 140);
        container.add(tProvider);

        pProvider = new JButton("  ...");
        pProvider.setFont(new Font("Arial", Font.PLAIN, 18));
        pProvider.setSize(50, 35);
        pProvider.setLocation(792, 140);
        container.add(pProvider);
    }

    private void setPhone() {
        phoneNum = new JLabel("<html><nobr>Phone Number <font color='#ffbebe'>*</font></nobr></html>");
        phoneNum.setFont(new Font("Arial", Font.PLAIN, 18));
        phoneNum.setSize(200, 30);
        phoneNum.setLocation(150, 180);
        container.add(phoneNum);

        phoneValidation = new JLabel("Invalid");
        phoneValidation.setFont(new Font("Arial", Font.PLAIN, 10));
        phoneValidation.setForeground(Color.RED);
        phoneValidation.setSize(40, 30);
        phoneValidation.setLocation(405, 180);
        phoneValidation.setVisible(false);
        container.add(phoneValidation);

        tPhoneNum = new JTextField();
        tPhoneNum.setFont(new Font("Arial", Font.PLAIN, 18));
        tPhoneNum.setSize(400, 30);
        tPhoneNum.setLocation(450, 180);
        container.add(tPhoneNum);
    }

    private void setFNameLName() {
        name = new JLabel("<html><nobr>First/Last Name <font color='#ffbebe'>*</font></nobr></html>");
        name.setFont(new Font("Arial", Font.PLAIN, 18));
        name.setSize(200, 30);
        name.setLocation(150, 220);
        container.add(name);

        nameValidation = new JLabel("Invalid");
        nameValidation.setFont(new Font("Arial", Font.PLAIN, 10));
        nameValidation.setForeground(Color.RED);
        nameValidation.setSize(40, 30);
        nameValidation.setLocation(405, 220);
        nameValidation.setVisible(false);
        container.add(nameValidation);

        tFName = new JTextField();
        tFName.setFont(new Font("Arial", Font.PLAIN, 18));
        tFName.setSize(199, 30);
        tFName.setLocation(450, 220);
        container.add(tFName);

        tLName = new JTextField();
        tLName.setFont(new Font("Arial", Font.PLAIN, 18));
        tLName.setSize(199, 30);
        tLName.setLocation(651, 220);
        container.add(tLName);
    }

    private void setCompanyCallingFrom() {
        companyCallingFrom = new JLabel("<html><nobr>Company Calling from <font color='#ffbebe'>*</font></nobr></html>");
        companyCallingFrom.setFont(new Font("Arial", Font.PLAIN, 18));
        companyCallingFrom.setSize(200, 30);
        companyCallingFrom.setLocation(150, 260);
        container.add(companyCallingFrom);

        companyValidation = new JLabel("Invalid");
        companyValidation.setFont(new Font("Arial", Font.PLAIN, 10));
        companyValidation.setForeground(Color.RED);
        companyValidation.setSize(40, 30);
        companyValidation.setLocation(405, 260);
        companyValidation.setVisible(false);
        container.add(companyValidation);

        tCompanyCallingFrom = new JTextField();
        tCompanyCallingFrom.setFont(new Font("Arial", Font.PLAIN, 18));
        tCompanyCallingFrom.setSize(400, 30);
        tCompanyCallingFrom.setLocation(450, 260);
        container.add(tCompanyCallingFrom);
    }

    private void setMember() {
        memberId = new JLabel("SSN/MemberID");
        memberId.setFont(new Font("Arial", Font.PLAIN, 18));
        memberId.setSize(400, 30);
        memberId.setLocation(150, 300);
        container.add(memberId);

        memberIdValidation = new JLabel("Invalid");
        memberIdValidation.setFont(new Font("Arial", Font.PLAIN, 10));
        memberIdValidation.setForeground(Color.RED);
        memberIdValidation.setSize(40, 30);
        memberIdValidation.setLocation(405, 300);
        memberIdValidation.setVisible(false);
        container.add(memberIdValidation);

        tMemberId = new JTextField();
        tMemberId.setFont(new Font("Arial", Font.PLAIN, 18));
        tMemberId.setSize(400, 30);
        tMemberId.setLocation(450, 300);
        container.add(tMemberId);
    }

    private void setClaimNum() {
        claimNumber = new JLabel("Claim Number");
        claimNumber.setFont(new Font("Arial", Font.PLAIN, 18));
        claimNumber.setSize(200, 30);
        claimNumber.setLocation(150, 340);
        container.add(claimNumber);

        claimNumberValidation = new JLabel("Invalid");
        claimNumberValidation.setFont(new Font("Arial", Font.PLAIN, 10));
        claimNumberValidation.setForeground(Color.RED);
        claimNumberValidation.setSize(40, 30);
        claimNumberValidation.setLocation(405, 340);
        claimNumberValidation.setVisible(false);
        container.add(claimNumberValidation);


        tClaimNumber = new JTextField();
        tClaimNumber.setFont(new Font("Arial", Font.PLAIN, 18));
        tClaimNumber.setSize(400, 30);
        tClaimNumber.setLocation(450, 340);
        container.add(tClaimNumber);
    }

    private void UpdateDocumentListener() {
        //  autoFile  document listener for memberId
        DocumentListener autoFillListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateName();
            }

            private void updateName() {
                String ssn = tMemberId.getText();
                if (ssn.length() == 9) {
                    Repository repo = Repository.getInstance("SALAHM");
                    String fullName = repo.getName(ssn);
                    if (fullName.length() > 2) {
                        String[] fullNameArray = fullName.split(",");
                        String fName = fullNameArray[0];
                        String lName = fullNameArray[1];
                        tFName.setText(fName);
                        tLName.setText(lName);
                    }
                } else {
                    tFName.setText("");
                    tLName.setText("");
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateName();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateName();
            }
        };
        tMemberId.getDocument().addDocumentListener(autoFillListener);


        //  Reference Number validation listener for memberId
        DocumentListener refNumValidationListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateRefNum();
            }

            private void validateRefNum() {
                String refNum = tReferenceNumber.getText();

                if (refNum.length() > 10) {
                    refNumValidation.setVisible(true);
                } else {
                    refNumValidation.setVisible(false);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateRefNum();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validateRefNum();
            }
        };
        tReferenceNumber.getDocument().addDocumentListener(refNumValidationListener);


        //  Phone Number validation listener for memberId
        DocumentListener phoneNumValidationListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validatePhoneNum();
            }

            private void validatePhoneNum() {
                String refNum = tPhoneNum.getText();

                if (refNum.length() > 10) {
                    phoneValidation.setVisible(true);
                } else {
                    phoneValidation.setVisible(false);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validatePhoneNum();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validatePhoneNum();
            }
        };
        tPhoneNum.getDocument().addDocumentListener(phoneNumValidationListener);


        //  namee validation listener for memberId
        DocumentListener nameValidationListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateName();
            }

            private void validateName() {
                String fName = tFName.getText();
                String lName = tLName.getText();

                if (fName.length() > 25 || lName.length() > 25) {
                    nameValidation.setVisible(true);
                } else {
                    nameValidation.setVisible(false);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateName();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validateName();
            }
        };
        tFName.getDocument().addDocumentListener(nameValidationListener);
        tLName.getDocument().addDocumentListener(nameValidationListener);


        //  ssn/memberId validation listener for memberId
        DocumentListener ssnValidationListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateMemberId();
            }

            private void validateMemberId() {
                String memberId = tMemberId.getText();

                if (memberId.length() > 9) {
                    memberIdValidation.setVisible(true);
                } else {
                    memberIdValidation.setVisible(false);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateMemberId();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validateMemberId();
            }
        };
        tMemberId.getDocument().addDocumentListener(ssnValidationListener);


        //  claimNumber validation listener
        DocumentListener claimNumValidationListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateClaimNum();
            }

            private void validateClaimNum() {
                String claimNumber = tClaimNumber.getText();
                Repository repo = Repository.getInstance(CustomerServiceApp.user);

                if (claimNumber.length() > 7 || !repo.validateClaimNumber(claimNumber)) {
                    claimNumberValidation.setVisible(true);
                } else {
                    claimNumberValidation.setVisible(false);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateClaimNum();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validateClaimNum();
            }
        };
        tClaimNumber.getDocument().addDocumentListener(claimNumValidationListener);

        cancel = new JButton("Exit");
        cancel.setFont(new Font("Arial", Font.PLAIN, 18));
        cancel.setSize(100, 35);
        cancel.setLocation(650, 387);
        container.add(cancel);

        submit = new JButton("Ok");
        submit.setFont(new Font("Arial", Font.PLAIN, 18));
        submit.setSize(100, 35);
        submit.setLocation(750, 387);
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Repository repo = Repository.getInstance(CustomerServiceApp.user);
                if (checkData()) {
                    System.out.println("submit button clicked");
                    CrmLogRecord record = getData();
                    repo.insertIntoCrmLog(record);
                }
//
            }
        });
        container.add(submit);

    }


}
