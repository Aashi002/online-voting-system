package com.example.model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class VotingSystem {

    private static final SessionFactory sessionFactory;
    
    static {
        try {
            sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    @SuppressWarnings("deprecation")
	private static void updateUser(Scanner scanner) {
        System.out.println("Update User Information:");
        System.out.print("Enter your voter ID: ");
        String voterId = scanner.nextLine();

        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            // Retrieve the user by voter ID
            Query<User> query = session.createQuery("FROM User WHERE vid = :voterId", User.class);
            query.setParameter("voterId", voterId);
            User user = query.uniqueResult();

            if (user != null) {
                System.out.print("Enter new name: ");
                String newName = scanner.nextLine();
                user.setName(newName);

                session.update(user);
                tx.commit();
                System.out.println("User information updated successfully!");
            } else {
                System.out.println("User not found.");
            }
        } catch (Exception e) {
            System.out.println("Error occurred during user update: " + e.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
	private static void deleteUser(Scanner scanner) {
        System.out.println("Delete User:");
        System.out.print("Enter your voter ID: ");
        String voterId = scanner.nextLine();

        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            // Retrieve the user by voter ID
            Query<User> query = session.createQuery("FROM User WHERE vid = :voterId", User.class);
            query.setParameter("voterId", voterId);
            User user = query.uniqueResult();

            if (user != null) {
                session.delete(user);
                tx.commit();
                System.out.println("User deleted successfully!");
            } else {
                System.out.println("User not found.");
            }
        } catch (Exception e) {
            System.out.println("Error occurred during user deletion: " + e.getMessage());
        }
    }
    
    
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Voting System!");
        
        // User Registration
        registerUser(scanner);

        // User Login
        User user = loginUser(scanner);

        if (user != null) {
            List<Party> parties = getParties();
            System.out.println("Available Parties -");

            for (Party party : parties) {
                System.out.println(party.getid() + ". " + party.getname());
            }

            // Party Selection
            int selectedPartyId = selectParty(scanner, parties);

            // Vote Counting
            voteForParty(selectedPartyId);
            System.out.println("Additional Options:");
            System.out.println("1. Update User Information");
            System.out.println("2. Delete User");

            int option = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (option) {
                case 1:
                    updateUser(scanner);
                    break;
                case 2:
                    deleteUser(scanner);
                    break;
                default:
                    System.out.println("Invalid option.");}
     
        } else {
            System.out.println("Login failed");
        }

        // Close resources
        scanner.close();
        sessionFactory.close(); 
      
    }

    @SuppressWarnings("deprecation")
	private static void registerUser(Scanner scanner) {
        System.out.println("User Registration:");
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Enter your voter ID: ");
        String voterId = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        User newUser = new User();
        newUser.setName(name);
        newUser.setVid(voterId);
        newUser.setPassword(password);

        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.save(newUser);
            tx.commit();
            System.out.println("User registered successfully!");
        } catch (Exception e) {
            System.out.println("Error occurred during user registration: " + e.getMessage());
        }
    }

    private static User loginUser(Scanner scanner) {
        System.out.println("User Login:");
        System.out.print("Enter your voter ID: ");
        String voterId = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE vid = :voterId AND password = :password", User.class);
            query.setParameter("voterId", voterId);
            query.setParameter("password", password);
            User user = query.uniqueResult();
            if (user != null) {
                System.out.println("Login successful!");
                return user;
            } else {
                System.out.println("Invalid credentials. Please try again.");
                return null;
            }
        }
    }

    private static int selectParty(Scanner scanner, List<Party> parties) {
        int selectedPartyId;
        while (true) {
            System.out.print("Enter the ID of the party you want to vote for: ");
            selectedPartyId = scanner.nextInt();
             // Declaring selectedPartyId as final

            boolean isValidPartyId =false;
            for (Party party : parties) {
                if (party.getid() == selectedPartyId) {
                    isValidPartyId = true;
                    break;
                }
            }
            if (!isValidPartyId) {
                System.out.println("Invalid party ID. Please select a valid party ID.");
            } else {
                break;
            }
        }
        return selectedPartyId;
    }

    @SuppressWarnings("deprecation")
    private static void voteForParty(int partyId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            Party party = session.get(Party.class, partyId);
            if (party != null) {
                int currentVoteCount = party.getvoteCount(); // Get the current vote count
                party.setvoteCount(currentVoteCount + 1); // Increment the vote count
                session.update(party);
                tx.commit();
            
                System.out.println("Vote cast successfully for party: " + party.getname());
                 {
                    displayElectionResult();
                }
            } else {
                System.out.println("Invalid party selected!");
            }
        }
    }

    private static void displayElectionResult() {
        try (Session session = sessionFactory.openSession()) {
            Query<Party> query = session.createQuery("FROM Party ORDER BY voteCount DESC", Party.class);
            List<Party> parties = query.getResultList();
            System.out.println("Election Result:");
            if (!parties.isEmpty()) {
                Party winner = parties.get(0);
                String voteCount = winner.getvoteCount() != null ? winner.getvoteCount().toString() : "0";
                System.out.println("Winner: " + winner.getname() + " with " + voteCount + " votes");
            } else {
                System.out.println("No votes recorded yet.");
            }
        }
    }

    private static List<Party> getParties() {
        try (Session session = sessionFactory.openSession()) {
            Query<Party> query = session.createQuery("FROM Party", Party.class);
            List<Party> parties = query.getResultList();

            // Set the names of the parties
            for (Party party : parties) {
             setPartyName(party);
            }
            return parties != null ? parties : new ArrayList<>();
        }
    }

    private static void setPartyName(Party party) {
    	
    	switch (party.getid()) {
        case 102:
            party.setname("BJP");
            break;
        case 104:
            party.setname("AAP");
            break;
        case 106:
            party.setname("NCP");
            break;
        case 103:
            party.setname("BSP");
            break;
        case 105:
            party.setname("CNP");
            break;
        default:
            party.setname("Unknown Party");
    }
    }

    }

    
